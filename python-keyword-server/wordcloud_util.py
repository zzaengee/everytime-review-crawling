import os
from collections import Counter
from soynlp.tokenizer import RegexTokenizer

def load_stopwords():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    stopword_path = os.path.join(current_dir, "data", "stopwords.txt")
    with open(stopword_path, 'r', encoding='utf-8') as f:
        return set(f.read().splitlines())

def load_emotion_words():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    emotion_path = os.path.join(current_dir, "data", "emotion_words.txt")
    with open(emotion_path, 'r', encoding='utf-8') as f:
        return set(f.read().splitlines())

def extract_keywords(text, stopwords, emotion_words):
    tokenizer = RegexTokenizer()
    words = tokenizer.tokenize(text)
    keywords = []
    for word in words:
        if word in emotion_words or (word not in stopwords and len(word) > 1):
            keywords.append(word)
    return keywords

def get_top_keywords(reviews, top_n=20):
    stopwords = load_stopwords()
    emotion_words = load_emotion_words()
    all_keywords = []
    for review in reviews:
        all_keywords.extend(extract_keywords(review, stopwords, emotion_words))
    counter = Counter(all_keywords)
    return [{"keyword": word, "count": count} for word, count in counter.most_common(top_n)]