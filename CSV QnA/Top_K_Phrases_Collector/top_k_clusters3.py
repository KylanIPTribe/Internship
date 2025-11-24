import base64
import pandas as pd
from io import StringIO

import numpy as np
from sklearn.feature_extraction.text import CountVectorizer, HashingVectorizer
from sklearn.cluster import KMeans
from sklearn.decomposition import TruncatedSVD
from sklearn.preprocessing import Normalizer
from sklearn.pipeline import make_pipeline


def sfs_to_df(sfs_data):
    if not isinstance(sfs_data, dict) or 'content' not in sfs_data:
        return None
    content = sfs_data['content']
    if not content:
        return []
    try:
        decoded_content = base64.b64decode(content).decode('utf-8')
    except Exception:
        decoded_content = content
    csv_buffer = StringIO(decoded_content)
    df = pd.read_csv(csv_buffer)
    return df


def do_bow(documents, min_phrase_length=1, max_phrase_length=3):
    count_vectorizer = CountVectorizer(
        lowercase=True,
        stop_words='english',
        ngram_range=(min_phrase_length, max_phrase_length),
        min_df=2,
        max_df=0.95
    )
    bow_matrix = count_vectorizer.fit_transform(documents)
    feature_names = count_vectorizer.get_feature_names_out()
    return bow_matrix, feature_names


def dimensionality_reduction(K, bow_matrix):
    lsa = make_pipeline(
        TruncatedSVD(n_components=min(100, K * 2)),  # Use fewer components if K is small
        Normalizer(copy=False)
    )
    bow_matrix_lsa = lsa.fit_transform(bow_matrix)
    # Convert sparse matrix to dense for easier manipulation downstream
    bow_matrix_dense = bow_matrix.toarray()  # Shape: (n_docs, n_features)
    return bow_matrix_lsa, bow_matrix_dense


def do_kmeans_clustering(K, documents, bow_matrix_lsa, bow_matrix_dense, feature_names):
    kmeans = KMeans(
        n_clusters=K,
        n_init=10,  # Multiple runs for better initialization
        max_iter=300,
        random_state=42
    )
    cluster_labels = kmeans.fit_predict(bow_matrix_lsa)
    # Calculate importance of each phrase within its cluster to balance intra-cluster relevance and cluster size
    # define importance as: (frequency in cluster / total frequency in corpus) * (cluster size / total documents)
    phrase_importance = {}
    # For each cluster, find the top phrases based on TF-IDF-like weighting within the cluster
    for cluster_id in range(K):
        # Get documents belonging to this cluster
        mask = (cluster_labels == cluster_id)
        cluster_doc_indices = np.where(mask)[0]
        # If no documents in this cluster, skip
        if len(cluster_doc_indices) == 0:
            continue
        # Compute the average TF (term frequency) of each word within this cluster
        cluster_tf = bow_matrix_dense[cluster_doc_indices].sum(axis=0)  # Sum across docs in cluster
        # Total number of documents in the dataset
        total_docs = len(documents)
        # Cluster size
        cluster_size = len(cluster_doc_indices)
        # For each phrase, calculate its importance
        for i, phrase in enumerate(feature_names):
            # Term frequency within cluster
            tf_in_cluster = cluster_tf[i]
            # Global term frequency across all documents
            global_tf = bow_matrix_dense[:, i].sum()
            # Avoid division by zero
            if global_tf == 0:
                continue
            # Weighted importance: higher if frequent in cluster and rare globally
            # Also penalize if cluster is very small
            importance = (tf_in_cluster / global_tf) * (cluster_size / total_docs)
            # Accumulate importance across clusters
            if phrase not in phrase_importance:
                phrase_importance[phrase] = 0
            phrase_importance[phrase] += importance
    phrase_percentage_pairs = [(phrase, score) for phrase, score in phrase_importance.items()]
    return phrase_percentage_pairs


def get_top_K(K, results):
    results.sort(key=lambda x: x[1], reverse=True)
    top_k_ngrams = results[:K]
    return top_k_ngrams


def handler(input_data):
    sfs_file = input_data.get("File")
    K = input_data.get("K", 5)
    df = sfs_to_df(sfs_file)
    documents = df.iloc[:, 1]  # use col 2

    bow_matrix, feature_names = do_bow(documents, 1, 3)
    bow_matrix_lsa, bow_matrix_dense = dimensionality_reduction(K, bow_matrix)
    phrase_percentage_pairs = do_kmeans_clustering(K, documents, bow_matrix_lsa, bow_matrix_dense, feature_names)
    top_k_ngrams = get_top_K(K, phrase_percentage_pairs)
    return top_k_ngrams