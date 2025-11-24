#!/usr/bin/env python3

import csv

def concatenate_csv_columns(input_file, output_file):
    """
    Concatenate strings from column 2 onwards for each row in a CSV file.
    
    Args:
        input_file (str): Path to the input CSV file
        output_file (str): Path to the output file where results will be saved
    """
    
    # Create a list to store the concatenated results
    concatenated_results = []
    header_questions = None
    
    # Try different encodings
    encodings = ['utf-8', 'latin-1', 'cp1252', 'iso-8859-1', 'utf-8-sig']
    
    for encoding in encodings:
        try:
            print(f"Trying encoding: {encoding}")
            with open(input_file, 'r', encoding=encoding, newline='') as csvfile:
                reader = csv.reader(csvfile)
                
                # Process each row
                for row_num, row in enumerate(reader):
                    if len(row) < 2:  # Skip rows with less than 2 columns
                        continue
                        
                    # Get the ID (first column)
                    row_id = row[0]
                    
                    # Get all columns from column 2 onwards (index 1 onwards)
                    columns_to_concatenate = row[1:]
                    
                    # Convert all values to strings and concatenate them with newlines, filtering out empty values
                    concatenated_string = '\n'.join(str(value).strip() for value in columns_to_concatenate if value and str(value).strip())
                    
                    # Store the result
                    concatenated_results.append([row_id, concatenated_string])
                    
                    # Capture the header row (first row) questions for the new header
                    if row_num == 0:
                        header_questions = concatenated_string
            
            print(f"Successfully read file with encoding: {encoding}")
            break
            
        except UnicodeDecodeError:
            print(f"Failed with encoding: {encoding}")
            concatenated_results = []  # Reset for next attempt
            continue
        except Exception as e:
            print(f"Error with encoding {encoding}: {str(e)}")
            concatenated_results = []  # Reset for next attempt
            continue
    
    if not concatenated_results:
        raise ValueError("Could not read the file with any of the tried encodings")
    
    # Write results to CSV file
    with open(output_file, 'w', encoding='utf-8', newline='') as csvfile:
        writer = csv.writer(csvfile)
        
        # Write header with ID and the concatenated questions
        writer.writerow(['ID', header_questions])
        
        # Write data (skip the first row since it was the header)
        writer.writerows(concatenated_results[1:])
    
    print(f"Concatenation complete! Results saved to: {output_file}")
    print(f"Processed {len(concatenated_results)} rows")
    
    return concatenated_results

def main():
    # File paths
    input_file = "files to reference/Help Us Shape the Next RCS Workshop.(1-12) edited.csv"
    output_file = "concatenated_results_final.csv"
    
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
