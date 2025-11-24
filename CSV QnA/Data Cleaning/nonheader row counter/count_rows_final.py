def count_non_header_rows_final(csv_file_path):
    """
    Count the number of non-header rows in a CSV file.
    This version correctly identifies that the header ends at line 13 and data starts at line 14.
    
    Args:
        csv_file_path (str): Path to the CSV file
        
    Returns:
        int: Number of non-header rows
    """
    try:
        with open(csv_file_path, 'r', encoding='utf-8') as file:
            lines = file.readlines()
            
        # Based on the file structure, header ends at line 13 (index 12)
        # Data starts at line 14 (index 13)
        header_lines = 13
        total_lines = len(lines)
        
        # Count non-header rows
        non_header_count = total_lines - header_lines
        
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
    non_header_count = count_non_header_rows_final(csv_file)
    
    print(f"Number of non-header rows: {non_header_count}")
    print(f"Total lines in file: {non_header_count + 13}")

if __name__ == "__main__":
    main()
