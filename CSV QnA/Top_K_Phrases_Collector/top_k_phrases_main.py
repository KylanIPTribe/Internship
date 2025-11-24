# import top_k_clusters as tkc  # bag-of-words
import top_k_clusters2 as tkc  # tf-idf
# import top_k_clusters3 as tkc  # k-means using bow and tfidf?
# import top_k_clusters4 as tkc  # k-means using tfidf?
import top_k_formatter as tkf
import pandas as pd
import base64
from io import StringIO


def csv_to_sfs_format(csv_file_path):
    # Try different encodings to read the CSV file
    encodings = ['utf-8', 'utf-8-sig', 'latin-1', 'cp1252', 'iso-8859-1']
    df = None
    
    for encoding in encodings:
        try:
            df = pd.read_csv(csv_file_path, encoding=encoding)
            break
        except UnicodeDecodeError:
            continue
    
    # If all encodings fail, use latin-1 as fallback
    if df is None:
        df = pd.read_csv(csv_file_path, encoding='latin-1')

    # Convert DataFrame to CSV string
    csv_buffer = StringIO()
    df.to_csv(csv_buffer, index=False)
    csv_content = csv_buffer.getvalue()

    # Create SFS format
    sfs_data = {
        "name": csv_file_path,
        "content": base64.b64encode(csv_content.encode('utf-8')).decode('utf-8')
    }
    return sfs_data


def main(csv_file_path, K=5):
    print(f"Processing CSV file: {csv_file_path}")
    try:
        sfs_data = csv_to_sfs_format(csv_file_path)
        # Use single parameter interface
        input_data = {
            "File": sfs_data,
            "K": K
        }
        processed_data = tkc.handler(input_data)
        final_string = tkf.handler(processed_data)
        print(final_string)
    except Exception as e:
        print(f"Error generating report: {e}")


if __name__ == "__main__":
    # Example usage - you can change this to your CSV file path
    # csv_file = "questionnaire responses.csv"
    csv_file = "survey data for sentiment-collector.csv"
    # You can specify K (number of top phrases) as the second parameter
    main(csv_file, "5")  # Get top 5 phrases