import os
from collections import Counter
from soynlp.tokenizer import RegexTokenizer

def load_stopwords():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    stopword_path = os.path.join(current_dir, "data", "stopwords.txt")  # ✅ 경로 수정
    with open(stopword_path, 'r', encoding='utf-8') as f:
        return set(f.read().splitlines())

def extract_keywords(text, stopwords):
    tokenizer = RegexTokenizer()  # ✅ soynlp의 정규표현식 기반 토크나이저
    words = tokenizer.tokenize(text)
    keywords = [word for word in words if word not in stopwords and len(word) > 1]
    return keywords

def get_top_keywords(reviews, top_n=20):
    stopwords = load_stopwords()
    all_keywords = []
    for review in reviews:
        all_keywords.extend(extract_keywords(review, stopwords))
    counter = Counter(all_keywords)
    return [{"keyword": word, "count": count} for word, count in counter.most_common(top_n)]