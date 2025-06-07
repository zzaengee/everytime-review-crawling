#!/usr/bin/env python
# coding: utf-8

# 형태소 분석기 mecab 설치 과정

# In[2]:


get_ipython().system('pip install konlpy')


# In[3]:


get_ipython().system('git clone https://github.com/SOMJANG/Mecab-ko-for-Google-Colab.git')


# In[4]:


cd Mecab-ko-for-Google-Colab


# In[5]:


get_ipython().system('bash install_mecab-ko_on_colab_light_220429.sh')


# In[6]:


get_ipython().system('pip install hgtk')


# In[7]:


from konlpy.tag import Mecab
mecab = Mecab()


# In[8]:


from konlpy.tag import Mecab

mecab = Mecab()

text = "아버지가방에들어가신다"

print("mecab 형태소 추출:", mecab.morphs(text))
print("mecab 명사 추출:", mecab.nouns(text))


# 통합된 강의평 전처리 (불용어 제거)

# In[10]:


from google.colab import drive
import pandas as pd

# 드라이브 마운트
drive.mount('/content/drive')


# In[12]:


# 통합 리뷰 파일 경로
file_path = '/content/drive/MyDrive/통합_강의평/통합_인간과사회_reviews.csv'

# CSV 파일 확인
df = pd.read_csv(file_path)
df.head()


# In[13]:


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

# In[14]:


get_ipython().system('apt-get update -qq')
get_ipython().system('apt-get install -y fonts-nanum')
get_ipython().system('apt-get install fonts-nanum* -qq')
import matplotlib.pyplot as plt
import matplotlib.font_manager as fm

# 폰트 경로 설정
font_path = '/usr/share/fonts/truetype/nanum/NanumGothic.ttf'
font_prop = fm.FontProperties(fname=font_path)

# 테스트용 그래프 출력
plt.title('한글 폰트 테스트', fontproperties=font_prop)
plt.plot([1, 2, 3], [1, 4, 9])
plt.xlabel('x축', fontproperties=font_prop)
plt.ylabel('y축', fontproperties=font_prop)
plt.show()


# 
# 
# 강의평 워드클라우드 생성 - 전체 키워드

# In[15]:


import matplotlib.pyplot as plt
import matplotlib.font_manager as fm
from wordcloud import WordCloud

# 한글 폰트 설정
font_path = '/usr/share/fonts/truetype/nanum/NanumGothic.ttf'
font_prop = fm.FontProperties(fname=font_path)

# 워드클라우드 생성 함수
def generate_wordcloud_from_keywords(keywords, title=None):
    text = " ".join(keywords)
    wordcloud = WordCloud(
        font_path=font_path,
        background_color='white',
        width=800, height=400
    ).generate(text)

    plt.figure(figsize=(10, 5))
    plt.imshow(wordcloud, interpolation='bilinear')
    plt.axis('off')
    if title:
        plt.title(title, fontproperties=font_prop, fontsize=16)  # 폰트 적용
    plt.show()

# 특정 강의 + 교수
target_lecture = "NGO와현대사회"
target_professor = "이지은"


# 해당 수업 리뷰 필터링
filtered = df[
    (df["lecture"] == target_lecture) &
    (df["professor"] == target_professor)
]

# 분석 및 시각화
if not filtered.empty:
    text = filtered["review"].values[0]
    keywords = extract_keywords(text, stopwords)
    print(f"키워드 ({len(keywords)}개):", keywords[:30])  # 일부 미리보기
    generate_wordcloud_from_keywords(keywords, title=f"{target_lecture} - {target_professor}")
else:
    print("해당 수업 없음")


# 강의평 워드클라우드 생성 - 상위 30개 키워드

# In[17]:


import matplotlib.pyplot as plt
import matplotlib.font_manager as fm
from wordcloud import WordCloud
from collections import Counter

# 한글 폰트 설정
font_path = '/usr/share/fonts/truetype/nanum/NanumGothic.ttf'
font_prop = fm.FontProperties(fname=font_path)

# 워드클라우드 생성 함수 (상위 top_n만 시각화)
def generate_wordcloud_from_keywords(keywords, title=None, top_n=15):
    counter = Counter(keywords)
    top_keywords = dict(counter.most_common(top_n))  # 상위 n개만 추출

    wordcloud = WordCloud(
        font_path=font_path,
        background_color='white',
        width=800,
        height=400
    ).generate_from_frequencies(top_keywords)

    plt.figure(figsize=(10, 5))
    plt.imshow(wordcloud, interpolation='bilinear')
    plt.axis('off')
    if title:
        plt.title(title, fontproperties=font_prop, fontsize=16)
    plt.show()

# 특정 강의 + 교수
target_lecture = "NGO와현대사회"
target_professor = "이지은"

# 해당 수업 리뷰 필터링
filtered = df[
    (df["lecture"] == target_lecture) &
    (df["professor"] == target_professor)
]

# 분석 및 시각화
if not filtered.empty:
    text = filtered["review"].values[0]
    keywords = extract_keywords(text, stopwords)
    print(f"키워드 총 {len(keywords)}개 중 상위 30개:\n", Counter(keywords).most_common(30))
    generate_wordcloud_from_keywords(keywords, title=f"{target_lecture} - {target_professor}", top_n=30)
else:
    print("해당 수업 없음")

