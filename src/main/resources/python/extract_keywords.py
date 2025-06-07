# extract_keywords.py
# 이 스크립트는 Java에서 subprocess로 호출되어
# JSON 형태의 리뷰 리스트를 입력받아 키워드 상위 20개를 JSON으로 출력함

import sys
import json
from wordcloud_util import load_stopwords, extract_keywords, get_top_keywords

# 리뷰 리스트를 표준 입력에서 받기
input_text = sys.stdin.read()
reviews = json.loads(input_text)

# 불용어 로드
stopwords = load_stopwords("stopwords.txt")

# 키워드 추출 및 빈도 집계
keywords = extract_keywords(reviews, stopwords)
top_keywords = get_top_keywords(keywords, top_n=20)

# 결과를 JSON 형식으로 출력
result = [{"keyword": kw, "count": count} for kw, count in top_keywords]
print(json.dumps(result, ensure_ascii=False))
