def count_non_header_rows_simple(csv_file_path):
    """
    Simple function to count non-header rows by reading lines directly.
    
    Args:
        csv_file_path (str): Path to the CSV file
        
    Returns:
        int: Number of non-header rows
    """
    try:
        with open(csv_file_path, 'r', encoding='utf-8') as file:
            lines = file.readlines()
            
        # Subtract 1 for the header row
        return len(lines) - 1
    
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
    non_header_count = count_non_header_rows_simple(csv_file)
    
    print(f"Number of non-header rows: {non_header_count}")

if __name__ == "__main__":
    main()
