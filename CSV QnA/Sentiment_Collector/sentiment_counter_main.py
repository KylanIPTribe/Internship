import make_sentiment_column2 as msc
import calculate_percentage
import pandas as pd
import base64
from io import StringIO

def csv_to_sfs_format(csv_file_path):
    """
    Convert a CSV file to SFS format that the modules expect
    """
    # Read the CSV file
    df = pd.read_csv(csv_file_path)
    
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

def main(csv_file_path):
    print(f"Processing CSV file: {csv_file_path}")
    try:
        sfs_data = csv_to_sfs_format(csv_file_path)
        processed_file = msc.handler(sfs_data)
        if processed_file:
            percentages = calculate_percentage.handler(processed_file)
        else:
            print("Error processing the file")
    except Exception as e:
        print(f"Error generating report: {e}")

if __name__ == "__main__":
    # Example usage - you can change this to your CSV file path
    csv_file = "cleaned data test input.csv"
    main(csv_file)




# Overall Sentiment: 
# - {{.num_positive}} are positive, which is {{.percent_positive}}%
# - {{.num_neutral}} are neutral, which is {{.percent_neutral}}%
# - {{.num_negative}} are negative, which is {{.percent_negative}}%

# Fielded: DD MMM 2025 – DD MMM 2025
# No. of responses collected: {{.total_responses}}
# 
# {{.overall_sentiment}} 