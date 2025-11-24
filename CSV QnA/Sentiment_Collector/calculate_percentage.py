import base64
import pandas as pd
from io import StringIO
import json
# python version 3.11


def sfs_to_df(sfs_data):
    if not isinstance(sfs_data, dict) or 'content' not in sfs_data:
        return []
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


def calculate_sentiment(df):
    sentiment_counts = df['sentiment'].value_counts().to_dict()

    total_responses = len(df)
    if total_responses == 0:
        return {
            "total_responses": 0,
            "num_positive": 0,
            "num_neutral": 0,
            "num_negative": 0,
            "percent_positive": 0,
            "percent_neutral": 0,
            "percent_negative": 0
        }

    num_positive = sentiment_counts.get("positive", 0)
    num_neutral = sentiment_counts.get("neutral", 0)
    num_negative = sentiment_counts.get("negative", 0)

    def to_percentage(fraction):
        return round(fraction * 100, 2)
    percent_positive = to_percentage(num_positive / total_responses)
    percent_neutral = to_percentage(num_neutral / total_responses)
    percent_negative = to_percentage(num_negative / total_responses)

    return {
        "total_responses": total_responses,
        "num_positive": num_positive,
        "num_neutral": num_neutral,
        "num_negative": num_negative,
        "percent_positive": percent_positive,
        "percent_neutral": percent_neutral,
        "percent_negative": percent_negative
    }


def handler(sfs_data):
    try:
        df_data = sfs_to_df(sfs_data)
        sentiment_percentages = calculate_sentiment(df_data)
        return sentiment_percentages
    except Exception as e:
        return None