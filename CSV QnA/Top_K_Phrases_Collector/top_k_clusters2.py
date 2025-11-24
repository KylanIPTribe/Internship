import base64
import pandas as pd
from io import StringIO
from sklearn.feature_extraction.text import TfidfVectorizer  # TF-IDF + n-grams
from typing import Dict, Any, List, Tuple
import numpy as np


def sfs_to_df(content):
    decoded_content = base64.b64decode(content).decode('utf-8')
    csv_buffer = StringIO(decoded_content)
    df = pd.read_csv(csv_buffer)
    return df


def do_tfidf(
    documents: List[str], 
    min_phrase_length: int = 1, 
    max_phrase_length: int = 3
) -> Tuple[np.ndarray, List[str]]:
    tfidf_vectorizer = TfidfVectorizer(
        lowercase=True,
        stop_words='english',
        ngram_range=(min_phrase_length, max_phrase_length),
        min_df=2,
        max_df=0.95
    )
    tfidf_matrix = tfidf_vectorizer.fit_transform(documents)
    feature_names = tfidf_vectorizer.get_feature_names_out()
    return tfidf_matrix, feature_names


def calc_percentages(
    total_respondents: int, 
    tfidf_matrix: np.ndarray, 
    feature_names: List[str]
) -> List[Tuple[str, float]]:
    phrase_freq_pairs = {}
    for i, phrase in enumerate(feature_names):
        # Binary count non-zero entries in the TF-IDF matrix for this phrase
        phrase_freq = (tfidf_matrix[:, i] > 0).sum()
        phrase_freq_pairs[phrase] = phrase_freq
    phrase_percentage_pairs = []
    for phrase, phrase_freq in phrase_freq_pairs.items():
        percentage = (phrase_freq / total_respondents) * 100
        phrase_percentage_pair = (phrase, percentage)
        phrase_percentage_pairs.append(phrase_percentage_pair)
    return phrase_percentage_pairs


def get_top_K(
    K: int, 
    results: List[Tuple[str, float]]
) -> List[Tuple[str, float]]:
    results.sort(key=lambda x: x[1], reverse=True)
    top_k_ngrams = results[:K]
    return top_k_ngrams


def handler(input_data):
    try:
        sfs_file = input_data.get("File").get("content")
        K = input_data.get("K", 5)
        df = sfs_to_df(sfs_file)
        documents = df.iloc[:, 1]  # use col 2

        tfidf_matrix, feature_names = do_tfidf(documents, 1, 3)
        phrase_percentage_pairs = calc_percentages(len(documents), tfidf_matrix, feature_names)
        top_k_ngrams = get_top_K(K, phrase_percentage_pairs)
        return top_k_ngrams
    except Exception as e:
        print(f"Error: {e}")
        return {"error": str(e)}