from konlpy.tag import Okt
from collections import Counter
import json

# 형태소 분석기 초기화
okt = Okt()

# 불용어 로딩 함수
def load_stopwords(path='stopwords.txt'):
    try:
        with open(path, encoding='utf-8') as f:
            return set(line.strip() for line in f if line.strip())
    except FileNotFoundError:
        return set()

# 키워드 추출 함수
def extract_keywords(texts, stopwords_path='stopwords.txt'):
    stopwords = load_stopwords(stopwords_path)
    keywords = []

    for text in texts:
        # 명사만 추출
        nouns = okt.nouns(text)
        # 길이 1 초과 단어만 포함
        filtered = [word for word in nouns if len(word) > 1 and word not in stopwords]
        keywords.extend(filtered)

    return keywords

# 상위 N개 키워드 추출
def get_top_keywords(keywords, n=20):
    counter = Counter(keywords)
    top_n = counter.most_common(n)
    return [{'keyword': k, 'count': v} for k, v in top_n]