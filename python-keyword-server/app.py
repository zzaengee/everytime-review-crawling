from flask import Flask, request, jsonify
import pandas as pd
import os
from wordcloud_util import get_top_keywords

app = Flask(__name__)

@app.route("/keywords", methods=["POST"])
def extract_keywords():
    data = request.get_json()
    lecture_key = data.get("lectureKey")  # 예: "대영RW"
    professor = data.get("professor")     # 예: "Breckenfeld John E"

    if not lecture_key or not professor:
        return jsonify({"error": "lectureKey and professor are required"}), 400

    # ✅ 파일 경로 조합
    filename = f"통합_{lecture_key}_reviews.csv"
    filepath = os.path.join("data", filename)

    if not os.path.exists(filepath):
        return jsonify({"error": f"File not found: {filename}"}), 404

    df = pd.read_csv(filepath)

    # ✅ 교수명 필터링
    filtered = df[df['professor'].str.strip() == professor.strip()]

    reviews = filtered['review'].dropna().tolist()
    if not reviews:
        return jsonify([])  # 키워드 없음

    # ✅ 키워드 추출
    top_keywords = get_top_keywords(reviews)
    return jsonify(top_keywords)