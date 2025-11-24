import base64
import pandas as pd
from io import StringIO

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans


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


def do_tfidf(documents, min_phrase_length=1, max_phrase_length=3):
    tfidf_vectorizer = TfidfVectorizer(
        lowercase=True,
        stop_words='english',
        ngram_range=(min_phrase_length, max_phrase_length),
        min_df=2
    )
    tfidf_matrix = tfidf_vectorizer.fit_transform(documents)
    feature_names = tfidf_vectorizer.get_feature_names_out()
    return tfidf_matrix, feature_names


def do_kmeans_clustering(K, tfidf_matrix):
    kmeans = KMeans(
        n_clusters=K,
        random_state=42
    )
    cluster_labels = kmeans.fit_predict(tfidf_matrix)
    return cluster_labels, kmeans


def get_top_terms_per_cluster(kmeans, feature_names, n_terms=10):
    top_terms = {}
    order_centroids = kmeans.cluster_centers_.argsort()[:, ::-1]
    for i in range(kmeans.n_clusters):
        top_terms[i] = []
        for ind in order_centroids[i, :n_terms]:
            top_terms[i].append(feature_names[ind])
    return top_terms


def handler(input_data):
    sfs_file = input_data.get("File")
    K = input_data.get("K", 5)
    df = sfs_to_df(sfs_file)
    documents = df.iloc[:, 1]  # use col 2

    tfidf_matrix, feature_names = do_tfidf(documents, 1, 3)
    cluster_labels, kmeans = do_kmeans_clustering(K, tfidf_matrix)
    top_terms_per_cluster = get_top_terms_per_cluster(kmeans, feature_names)