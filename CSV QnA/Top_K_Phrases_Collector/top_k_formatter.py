# no imports needed

def handler(top_phrases):
    K = len(top_phrases)
    lines = [f"The top {K} Phrases are:"]
    for item in top_phrases:
        line = f"• ??? — {item[1]:.2f}% mention: {item[0]}"
        lines.append(line)
    return "\n".join(lines)