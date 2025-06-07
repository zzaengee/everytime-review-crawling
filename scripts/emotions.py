#!/usr/bin/env python
# coding: utf-8

# 형태소 분석기 mecab 설치 과정

# In[1]:


get_ipython().system('pip install konlpy')


# In[2]:


get_ipython().system('git clone https://github.com/SOMJANG/Mecab-ko-for-Google-Colab.git')


# In[3]:


cd Mecab-ko-for-Google-Colab


# In[4]:


get_ipython().system('bash install_mecab-ko_on_colab_light_220429.sh')


# In[5]:


get_ipython().system('pip install hgtk')


# In[6]:


from konlpy.tag import Mecab
mecab = Mecab()


# In[7]:


from konlpy.tag import Mecab

mecab = Mecab()

text = "아버지가방에들어가신다"

print("mecab 형태소 추출:", mecab.morphs(text))
print("mecab 명사 추출:", mecab.nouns(text))


# In[8]:


from google.colab import drive
import pandas as pd

# 드라이브 마운트
drive.mount('/content/drive')


# In[9]:


from konlpy.tag import Mecab

mecab = Mecab()

# 불용어 리스트 파일 경로
stopwords_path = '/content/drive/MyDrive/stopwords_v5.txt'

# 불용어 리스트 파일 읽어오기
with open(stopwords_path, encoding='utf-8') as f:
    stopwords = set(line.strip() for line in f if line.strip())

print(f"불용어 {len(stopwords)}개 불러옴")

mecab = Mecab()

def extract_keywords(text, stopwords_set):
    nouns = mecab.nouns(text)
    return [word for word in nouns if word not in stopwords_set and len(word) > 1]  # 한 글자 단어 제거


# 시각화용 한글 폰트 설치

# In[10]:


get_ipython().system('apt-get update -qq')
get_ipython().system('apt-get install -y fonts-nanum')
get_ipython().system('apt-get install fonts-nanum* -qq')
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm

# 폰트 경로 설정
font_path = '/usr/share/fonts/truetype/nanum/NanumGothic.ttf'
font_prop = fm.FontProperties(fname=font_path)

# 그래프 출력
plt.title('한글 폰트 테스트', fontproperties=font_prop)
plt.plot([1, 2, 3], [1, 4, 9])
plt.xlabel('x축', fontproperties=font_prop)
plt.ylabel('y축', fontproperties=font_prop)
plt.show()


# 감정분석 - KcELECTRA  모델
# https://github.com/jaehyeongAN/KoELECTRA-finetuned-sentiment-analysis

# In[11]:


get_ipython().system('pip uninstall -y transformers')


# In[12]:


get_ipython().system('pip install transformers==4.40.1')


# In[13]:


# import library
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification, TextClassificationPipeline

# load model
tokenizer = AutoTokenizer.from_pretrained("Copycats/koelectra-base-v3-generalized-sentiment-analysis")
model = AutoModelForSequenceClassification.from_pretrained("Copycats/koelectra-base-v3-generalized-sentiment-analysis")
sentiment_classifier = TextClassificationPipeline(tokenizer=tokenizer, model=model)


# In[14]:


review_list = [
	'수업이 유익하고 재미있습니다.'
]

for idx, review in enumerate(review_list):
  pred = sentiment_classifier(review)
  print(f'{review}\n>> {pred[0]}')


# In[15]:


import torch.nn.functional as F

def get_positive_score(text):
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=128)
    inputs = {k: v.to(model.device) for k, v in inputs.items()}

    with torch.no_grad():
        outputs = model(**inputs)
        logits = outputs.logits
        probs = F.softmax(logits, dim=1)         # [부정확률, 긍정확률]
        positive_score = probs[0][1].item()       # index 1 = 긍정

    return round(positive_score, 4)


# In[16]:


from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
import torch.nn.functional as F

# 모델 불러오기
model_name = "Copycats/koelectra-base-v3-generalized-sentiment-analysis"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForSequenceClassification.from_pretrained(model_name)

# GPU 설정
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)


# In[17]:


def get_positive_score(text):
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=128)
    inputs = {k: v.to(device) for k, v in inputs.items()}

    with torch.no_grad():
        logits = model(**inputs).logits
        probs = F.softmax(logits, dim=1)
        positive_score = probs[0][1].item()  # index 1 = 긍정 확률

    return round(positive_score, 4)


# In[18]:


# 감정 점수 실험용 예시 텍스트
review_list = [
    "수업이 아주 지루했어요.",
    "교수님이 열정적이고 설명도 잘 해주세요.",
    "강의는 평범했지만 도움이 되긴 했어요.",
    "시험 범위가 너무 불명확하고 과제가 많아요.",
    "이 강의는 정말 강추합니다! 너무 유익해요."
]

# 결과 출력
for review in review_list:
    score = get_positive_score(review)
    print(f"[{review}]\n→ 긍정 점수: {score}\n")


# In[25]:


# 데이터 값과 그 타입을 함께 출력
df["review_type"] = df["review"].apply(lambda x: type(x).__name__)
print(df["review_type"].value_counts())


# 교양 분류별 교수님 랭킹 / 강의별 교수님 랭킹

# In[34]:


# 전체 랭킹
# 파일 불러오기
import pandas as pd
df = pd.read_csv('/content/drive/MyDrive/문화와예술_reviews_labeled.csv')

# 강의 + 교수 결합 키 생성
df["lecture_professor"] = df["lecture"] + " - " + df["professor"]

# 문자열이 아닌 리뷰 제거
df = df[df["review"].apply(lambda x: isinstance(x, str))].reset_index(drop=True)

# 긍정 점수 계산 함수
def get_positive_score(text):
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=128)
    inputs = {k: v.to(model.device) for k, v in inputs.items()}
    with torch.no_grad():
        logits = model(**inputs).logits
        probs = F.softmax(logits, dim=1)
        positive_score = probs[0][1].item()  # index 1 = 긍정 확률
    return round(positive_score, 4)

# 전체 리뷰에 긍정 점수 적용
df["positive_score"] = df["review"].apply(get_positive_score)

# 강의별 평균 긍정 점수 계산
rank_df = df.groupby("lecture_professor")["positive_score"].mean().reset_index()
rank_df = rank_df.sort_values(by="positive_score", ascending=False)

# lecture_professor 컬럼 분리
rank_df[["lecture", "professor"]] = rank_df["lecture_professor"].str.split(" - ", expand=True)

# 평균 감정 상태 분류 함수 추가
def classify_sentiment(score):
    if score >= 0.7:
        return "긍정"
    elif score <= 0.3:
        return "부정"
    else:
        return "중립"

# 감정 상태 컬럼 추가
rank_df["avg_sentiment"] = rank_df["positive_score"].apply(classify_sentiment)

# 컬럼 순서 정렬
rank_df = rank_df[["lecture", "professor", "positive_score", "avg_sentiment"]]

# 상위 10개 출력
print(rank_df.head(10))


# In[26]:


# 저장 (선택)
#rank_df.to_csv('/content/drive/MyDrive/문화와예술_강의_긍정순위_감정상태.csv', index=False)
# df.to_csv('/content/drive/MyDrive/문화와예술_reviews_with_scores.csv', index=False)

# print("저장 완료")


# In[35]:


# 강의별 랭킹

# 각 강의-교수별 긍정 점수 평균 구하기
lecture_professor_score = df.groupby(["lecture", "professor"])["positive_score"].mean().reset_index()

# 강의별로 교수님 점수 순위 매기기
lecture_professor_score["rank_within_lecture"] = lecture_professor_score.groupby("lecture")["positive_score"]\
    .rank(ascending=False, method="first")

# 정렬
lecture_professor_score = lecture_professor_score.sort_values(by=["lecture", "rank_within_lecture"])

# 평균 감정 상태 분류 함수
def classify_sentiment(score):
    if score >= 0.7:
        return "긍정"
    elif score <= 0.3:
        return "부정"
    else:
        return "중립"

# 감정 상태 컬럼 추가
lecture_professor_score["avg_sentiment"] = lecture_professor_score["positive_score"].apply(classify_sentiment)

# 상위 10개 출력
print(lecture_professor_score.head(10))


# In[28]:


# 저장 (선택)
# lecture_professor_score.to_csv('/content/drive/MyDrive/문화와예술_강의별_교수별_긍정순위.csv', index=False)

# print("저장 완료")

