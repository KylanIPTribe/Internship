import base64
import csv
from io import StringIO
import pandas as pd
from transformers import pipeline
# python version 3.11


def sfs_to_df(content):
    try:
        decoded_content = base64.b64decode(content).decode('utf-8')
        csv_buffer = StringIO(decoded_content)
        df = pd.read_csv(csv_buffer)
        return df
    except Exception as e:
        return f"Exception occurred in sfs_to_df: {e}"


def df_to_sfs(df_data):
    try:
        # Convert data to CSV format
        headers = list(df_data[0].keys())
        # Create CSV content
        csv_buffer = StringIO()
        writer = csv.DictWriter(csv_buffer, fieldnames=headers)
        writer.writeheader()
        writer.writerows(df_data)
        csv_content = csv_buffer.getvalue()
        return {
            "name": 'data_with_sentiments.csv',
            "content": base64.b64encode(csv_content.encode('utf-8')).decode('utf-8')
        }
    except Exception as e:
        return f"Exception occurred in df_to_sfs: {e}"


def get_sentiments(raw_data, model="sentiment-analysis"):
    try:
        dict_data = [{"text": raw_datum} for raw_datum in raw_data.tolist()]
        sentiment_pipeline = pipeline(model)
        results = sentiment_pipeline(dict_data)
        sentiment_labels = [result["label"] for result in results]
        return sentiment_labels
    except Exception as e:
        return f"Exception occurred in get_sentiments: {e}"


def handler(sfs_data):
    try:
        df_data = sfs_to_df(sfs_data)
        data_to_analyze = df_data.iloc[:, 1]  # convert col 2 to a list
        sentiment_list = get_sentiments(data_to_analyze)
        df_data["sentiments"] = sentiment_list
        return df_to_sfs(df_data)
    except Exception as e:
        return None