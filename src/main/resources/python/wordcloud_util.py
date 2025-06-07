
# requirements:
# pip install konlpy wordcloud

from konlpy.tag import Mecab
from collections import Counter

# Mecab 객체 초기화
mecab = Mecab()

# 불용어 파일 로딩
def load_stopwords(filepath="stopwords.txt"):
    with open(filepath, encoding='utf-8') as f:
        return set(line.strip() for line in f if line.strip())

# 텍스트에서 명사 추출 및 불용어 제거
def extract_keywords(texts, stopwords):
    all_keywords = []
    for text in texts:
        nouns = mecab.nouns(text)
        filtered = [word for word in nouns if word not in stopwords and len(word) > 1]
        all_keywords.extend(filtered)
    return all_keywords

# 상위 키워드 빈도수 계산 (예: 20개)
def get_top_keywords(keywords, top_n=20):
    counter = Counter(keywords)
    return counter.most_common(top_n)
