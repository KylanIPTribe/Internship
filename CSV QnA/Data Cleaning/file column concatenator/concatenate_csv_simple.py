#!/usr/bin/env python3

import csv
import chardet
from pathlib import Path

def detect_file_encoding(file_path: Path, sample_bytes: int = 200_000) -> str:
    """Detect file encoding using chardet"""
    with open(file_path, 'rb') as f:
        raw = f.read(sample_bytes)
    result = chardet.detect(raw) or {}
    encoding = (result.get('encoding') or 'utf-8').lower()
    # Normalize common aliases
    if encoding in {'ascii'}:
        encoding = 'utf-8'
    return encoding

def concatenate_csv_columns(input_file, output_file):
    """
    Concatenate strings from column 2 onwards for each row in a CSV file.
    
    Args:
        input_file (str): Path to the input CSV file
        output_file (str): Path to the output file where results will be saved
    """
    
    input_path = Path(input_file)
    
    # Detect encoding
    encoding = detect_file_encoding(input_path)
    print(f"Detected encoding: {encoding}")
    
    # Create a list to store the concatenated results
    concatenated_results = []
    
    # Read the CSV file with detected encoding
    try:
        with open(input_path, 'r', encoding=encoding, newline='') as csvfile:
            reader = csv.reader(csvfile)
            
            # Process each row
            for row_num, row in enumerate(reader):
                if len(row) < 2:  # Skip rows with less than 2 columns
                    continue
                    
                # Get the ID (first column)
                row_id = row[0]
                
                # Get all columns from column 2 onwards (index 1 onwards)
                columns_to_concatenate = row[1:]
                
                # Convert all values to strings and concatenate them, filtering out empty values
                concatenated_string = ' '.join(str(value).strip() for value in columns_to_concatenate if value and str(value).strip())
                
                # Store the result
                concatenated_results.append([row_id, concatenated_string])
                
    except UnicodeDecodeError:
        # Fallback to latin-1 if detected encoding fails
        print("Detected encoding failed, trying latin-1...")
        with open(input_path, 'r', encoding='latin-1', newline='') as csvfile:
            reader = csv.reader(csvfile)
            
            # Process each row
            for row_num, row in enumerate(reader):
                if len(row) < 2:  # Skip rows with less than 2 columns
                    continue
                    
                # Get the ID (first column)
                row_id = row[0]
                
                # Get all columns from column 2 onwards (index 1 onwards)
                columns_to_concatenate = row[1:]
                
                # Convert all values to strings and concatenate them, filtering out empty values
                concatenated_string = ' '.join(str(value).strip() for value in columns_to_concatenate if value and str(value).strip())
                
                # Store the result
                concatenated_results.append([row_id, concatenated_string])
    
    # Write results to CSV file
    with open(output_file, 'w', encoding='utf-8', newline='') as csvfile:
        writer = csv.writer(csvfile)
        
        # Write header
        writer.writerow(['ID', 'Concatenated_Text'])
        
        # Write data
        writer.writerows(concatenated_results)
    
    print(f"Concatenation complete! Results saved to: {output_file}")
    print(f"Processed {len(concatenated_results)} rows")
    
    return concatenated_results

def main():
    # File paths
    input_file = "files to reference/Help Us Shape the Next RCS Workshop.(1-12) edited.csv"
    output_file = "concatenated_results.csv"
    
    try:
        # Perform the concatenation
        results = concatenate_csv_columns(input_file, output_file)
        
        # Display first few results
        print("\nFirst 3 results:")
        for i, (row_id, text) in enumerate(results[:3]):
            print(f"ID: {row_id}")
            print(f"Text: {text[:200]}{'...' if len(text) > 200 else ''}")
            print("-" * 50)
        
    except FileNotFoundError:
        print(f"Error: Input file '{input_file}' not found.")
    except Exception as e:
        print(f"An error occurred: {str(e)}")

if __name__ == "__main__":
    main()



