import csv

def count_non_header_rows(csv_file_path):
    """
    Count the number of non-header rows in a CSV file.
    
    Args:
        csv_file_path (str): Path to the CSV file
        
    Returns:
        int: Number of non-header rows
    """
    try:
        with open(csv_file_path, 'r', encoding='utf-8') as file:
            csv_reader = csv.reader(file)
            
            # Skip the header row
            next(csv_reader)
            
            # Count the remaining rows
            row_count = sum(1 for row in csv_reader)
            
        return row_count
    
    except FileNotFoundError:
        print(f"Error: File '{csv_file_path}' not found.")
        return 0
    except Exception as e:
        print(f"Error reading file: {e}")
        return 0

def main():
    # Path to the CSV file
    csv_file = "output files/concatenated_results_final.csv"
    
    # Count non-header rows
    non_header_count = count_non_header_rows(csv_file)
    
    print(f"Number of non-header rows: {non_header_count}")

if __name__ == "__main__":
    main()
