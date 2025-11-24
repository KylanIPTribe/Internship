import base64
import csv
from io import StringIO
import requests
# python version 3.11

def sfs_to_csv(sfs_data):
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
    reader = csv.DictReader(csv_buffer)
    return list(reader)
def csv_to_sfs(csv_data, original_sfs):
    # Convert data to CSV format
    headers = list(csv_data[0].keys())
    # Create CSV content
    csv_buffer = StringIO()
    writer = csv.DictWriter(csv_buffer, fieldnames=headers)
    writer.writeheader()
    writer.writerows(csv_data)
    csv_content = csv_buffer.getvalue()
    return {
        "name": 'data_with_sentiments.csv',
        "content": base64.b64encode(csv_content.encode('utf-8')).decode('utf-8')
    }

def get_sentiment(text):
    # try:
    #     url = "https://api.jigsawstack.com/v1/ai/sentiment"
    #     headers = {
    #         "x-api-key": "",
    #         "Content-Type": "application/json"
    #     }
    #     data = { "text": text }
    #     response = requests.post(url, headers=headers, json=data, stream=False, timeout=30).json()

    #     print("after getting a sentiment \n")
    #     sentiment = response.get('sentiment', {}).get('sentiment', 'unknown')
    #     if sentiment.lower() in ['positive']:
    #         return 'positive'
    #     elif sentiment.lower() in ['negative']:
    #         return 'negative'
    #     else:
    #         return 'neutral'
    # except Exception as e:
    #     print(f"Error analyzing sentiment: {str(e)}")
    #     return 'neutral'
    return "neutral"  #NO ISSUES HERE I THINK
def get_sentiments(csv_data):
    for row in csv_data:
        # Get the first two columns (header names unknown); Analyze sentiment only for the 2nd column
        columns = list(row.keys())
        text = str(row[columns[1]]) # if row[columns[1]] is not None else ""

        if text.strip():
            row['sentiment'] = get_sentiment(text.strip())
        else:
            row['sentiment'] = ""

        # time.sleep(1)  # Rate limiting does not work

def handler(sfs_data):
    try:
        csv_data = sfs_to_csv(sfs_data)
        if not csv_data:
            return None
        get_sentiments(csv_data)
        return csv_to_sfs(csv_data, sfs_data)
    except Exception as e:
        return None