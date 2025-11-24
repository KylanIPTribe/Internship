import base64
import pandas as pd
from io import StringIO
from sklearn.feature_extraction.text import CountVectorizer  # bag-of-words + n-grams


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


def calc_percentages(total_respondents, bow_matrix, feature_names):
    phrase_freq_pairs = {}
    for i, phrase in enumerate(feature_names):
        # Binary count non-zero entries in the bow matrix for this phrase
        phrase_freq = (bow_matrix[:, i] > 0).sum()
        phrase_freq_pairs[phrase] = phrase_freq
    phrase_percentage_pairs = []
    for phrase, count in phrase_freq_pairs.items():
        percentage = (count / total_respondents) * 100
        phrase_percentage_pair = (phrase, percentage)
        phrase_percentage_pairs.append(phrase_percentage_pair)
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
    phrase_percentage_pairs = calc_percentages(len(documents), bow_matrix, feature_names)
    top_k_ngrams = get_top_K(K, phrase_percentage_pairs)
    return top_k_ngrams