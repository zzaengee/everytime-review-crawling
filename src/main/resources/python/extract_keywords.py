import sys
import json
from wordcloud_util import extract_keywords, get_top_keywords, load_stopwords

def main():
    reviews = json.load(sys.stdin)
    text_data = " ".join(reviews)

    stopwords = load_stopwords("data/stopwords.txt")
    keywords = extract_keywords(text_data, stopwords)
    top_keywords = get_top_keywords(keywords, top_n=20)

    print(json.dumps(top_keywords, ensure_ascii=False))

if __name__ == "__main__":
    main()