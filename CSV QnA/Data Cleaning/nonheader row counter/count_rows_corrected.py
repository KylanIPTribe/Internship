import csv

def count_non_header_rows_corrected(csv_file_path):
    """
    Count the number of non-header rows in a CSV file.
    This version handles multi-line headers properly.
    
    Args:
        csv_file_path (str): Path to the CSV file
        
    Returns:
        int: Number of non-header rows
    """
    try:
        with open(csv_file_path, 'r', encoding='utf-8') as file:
            lines = file.readlines()
            
        # Find where the actual data starts by looking for the first line that starts with a number (ID)
        data_start_line = 0
        for i, line in enumerate(lines):
            line = line.strip()
            if line and line[0].isdigit():
                data_start_line = i
                break
        
        # Count non-header rows (total lines minus header lines)
        non_header_count = len(lines) - data_start_line
        
        return non_header_count
    
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
    non_header_count = count_non_header_rows_corrected(csv_file)
    
    print(f"Number of non-header rows: {non_header_count}")

if __name__ == "__main__":
    main()
