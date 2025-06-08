from flask import Flask, request, jsonify
import pandas as pd
import os
from wordcloud_util import get_top_keywords, extract_keywords, load_stopwords  # ✅ 함수들 임포트

app = Flask(__name__)


@app.route("/keywords", methods=["POST"])
def extract_keywords_api():
    data = request.get_json()
    lecture_key = data.get("lectureKey")
    professor = data.get("professor")

    if not lecture_key or not professor:
        return jsonify({"error": "lectureKey and professor are required"}), 400

    filename = f"통합_{lecture_key}_reviews.csv"
    filepath = os.path.join("data", filename)

    if not os.path.exists(filepath):
        return jsonify({"error": f"File not found: {filename}"}), 404

    df = pd.read_csv(filepath)
    filtered = df[df['professor'].str.strip() == professor.strip()]
    reviews = filtered['review'].dropna().tolist()

    if not reviews:
        return jsonify([])

    # ✅ 불용어 로딩
    stopwords = load_stopwords()

    # ✅ 전체 리뷰에서 키워드 추출
    all_keywords = []
    for review in reviews:
        all_keywords.extend(extract_keywords(review, stopwords))

    # ✅ 상위 키워드 추출
    top_keywords = get_top_keywords(all_keywords)

    return jsonify(top_keywords)

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5050))
    app.run(host="0.0.0.0", port=port)