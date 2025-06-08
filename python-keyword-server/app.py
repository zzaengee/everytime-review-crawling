from flask import Flask, request, jsonify
from konlpy.tag import Okt
from collections import Counter
import json

app = Flask(__name__)

# 불용어 로딩
def load_stopwords(path="data/stopwords.txt"):
    with open(path, "r", encoding="utf-8") as f:
        return set(line.strip() for line in f if line.strip())

stopwords = load_stopwords()
okt = Okt()

# 키워드 추출 함수
def extract_keywords(texts, top_n=20):
    words = []
    for text in texts:
        tokens = okt.nouns(text)
        filtered = [word for word in tokens if word not in stopwords and len(word) > 1]
        words.extend(filtered)

    counter = Counter(words)
    return counter.most_common(top_n)

@app.route("/keywords", methods=[POST])
def get_keywords():
    try:
        data = request.get_json()
        if not isinstance(data, list):
            return jsonify({"error": "Expected a list of reviews"}), 400

        top_keywords = extract_keywords(data)
        response = [{"keyword": k, "count": v} for k, v in top_keywords]
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)