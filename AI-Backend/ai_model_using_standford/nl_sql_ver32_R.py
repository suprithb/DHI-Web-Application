#!/usr/bin/env python
# coding: utf-8

# In[1]:


from flask import jsonify
from visualization import out_put
from Graph_renderer_V1_9 import preparing_VIZ_output_df
import numpy as np
import pandas as pd
import itertools
from nltk.corpus import stopwords
import nltk
from autocorrect import spell
from nltk import bigrams

from  nltk.util import ngrams
import re
from stanfordcorenlp import StanfordCoreNLP
from nltk.parse import CoreNLPParser
from nltk.parse.corenlp import CoreNLPDependencyParser
from nltk import word_tokenize
import sqlite3
from nltk.corpus import wordnet
import datefinder
import re
import datetime
from datetime import date , datetime , timedelta
from nltk.tokenize import sent_tokenize, word_tokenize
from nltk.tag.stanford import StanfordNERTagger
import pandasql as ps
import pysqldf

from nltk.stem.lancaster import LancasterStemmer
from sklearn.preprocessing import OneHotEncoder
import matplotlib.pyplot as plt
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences
from keras.utils import to_categorical
from keras.models import Sequential, load_model
from keras.layers import Dense, LSTM, Bidirectional, Embedding, Dropout
from keras.callbacks import ModelCheckpoint
import random


nltk.download("stopwords")
nltk.download("punkt")


# In[2]:


# method to take input from customer 
def get_input():
    sent =input()
    return sent


# In[3]:


# function for getting bi-grams
def get_bigrams(sent):
    tokens = nltk.word_tokenize(sent)
    return zip(tokens,tokens[1:])


# In[4]:


#Function to get POS tagger
def get_pos_tag(core_token):
    pos_tagger =  CoreNLPParser(url ='http://localhost:9000', tagtype = 'pos')
    core_pos_tagger = pos_tagger.tag(core_token)
    #core_pos_tagger = nltk.pos_tag(core_token)
    return core_pos_tagger


# In[5]:


def load_data_file(data_file):
    file_type = data_file.split(".")[-1]
    #print(file_type)
    if file_type == 'csv':
        df_data = pd.read_csv(data_file)
        #df2 = df.apply(lambda x: x.astype(str).str.upper())
        df_data1= df_data.apply(lambda x:x.astype(str).str.lower())
        
    elif file_type == 'xlsx':
        df_data=pd.read_excel(data_file)
        df_data1 = df_data.apply(lambda x:x.astype(str).str.lower())
    elif file_type == 'txt':
        import csv
        with open(data_file,'r') as f1:
            dialect = csv.Sniffer().sniff(f1.read())
            f1.seek(0)
            df_data= pd.read_csv(f1, delimiter=dialect.delimiter )
            df_data1 = df_data.apply(lambda x:x.astype(str).str.lower())
    return df_data1


# In[6]:


#NER tagger
def get_ner_tag(core_token):
    #import os
    #path = "C:/Program Files/Java/jdk1.8.0_211/bin/java.exe"
    #os.environ['JAVAHOME'] = path
    #jar = 'C:/Users/himanshu.b/stanford_jar/stanford-ner.jar'
    #model = 'C:/Users/himanshu.b/stanford_jar/english.all.3class.distsim.crf.ser.gz'
    #ner_tagger = StanfordNERTagger(model, jar, encoding='utf8')
    #st = StanfordNERTagger(model,jar)
    #core_ner_tagger = st.tag(core_token)
    ner_tagger =  CoreNLPParser(url ='http://localhost:9000', tagtype = 'ner')
    core_ner_tagger = ner_tagger.tag(core_token)
    #core_pos_tagger = nltk.pos_tag(core_token)
    #core_ner_tagger = nltk.ne_chunk(core_pos_tagger)
    #print(core_pos_tagger)
    #print(core_ner_tagger)
    return core_ner_tagger


# In[92]:


#Forming lexicons
initial_lexicon = ['show','give','select','which','what']
select_lexicon = ['all records','*','full','whole records','complete','records']
top_lexicon = ['top','highest','first','best','topmost','upmost','uppermost']
bottom_lexicon = ['bottom','last','lowest','worst','least','lowermost']
aggregate_lexicon = ['sum','by','amount','summation','volume','count','average','mean','number','total']
operator_lexicon = ['less than','greater than','more than','lesser than','lower than','higher than','above','equal','equal to','equals','as','is']
order_lexicon = ['ascending','asc','descending','desc']
visual_lexicon =['trend','bar graph','bar','stacked graph','line graph','time_series','correlation','distribution','relationship']
conjunction_lexicon =['and','or''not']
date_lexicon =['day','days','week','weeks','month','months','quarter','quarters']
having_lexicon=['having','with','where','for']


# In[8]:


def get_date_lex_value(t):
    date=list()
    for i in t :
        if i in date_lexicon:
            date.append('True')
        else:
            date.append('False')
    return date

def get_conj_lex_value(t):
    conj =list()
    for i in t :
        if i in conjunction_lexicon:
            conj.append('True')
        else:
            conj.append('False')
    return conj

def get_initial_lex_value(t):
    init =list()
    for i in t :
        if i in initial_lexicon:
            init.append('True')
        else:
             init.append('False')
    return init
            
def get_select_lex_value(t):
    select = list()
    for i in t :
        if i in select_lexicon:
            select.append('True')
        else:
            select.append('False')
    return select

def get_top_lex_value(t):
    top = list()
    for i in t :
        if i in top_lexicon:
            top.append('True')
        else:
            top.append('False')
    return top
        
def get_bottom_lex_value(t):
    bottom = list()
    for i in t :
        if i in bottom_lexicon:
            bottom.append('True')
        else:
            bottom.append('False')
    return bottom

def get_aggregate_lex_value(t):
    aggregate= list()
    for i in t :
        if i in aggregate_lexicon:
            aggregate.append('True')
        else:
            aggregate.append('False')
    return aggregate

def get_operator_lex_value(t):
    operator= list()
    for i in t :
        if i in operator_lexicon:
            operator.append('True')
        else:
            operator.append('False')
    return operator 

def get_visual_lex_value(t):
    visual = list()
    for i in t :
        if i in visual_lexicon:
            visual.append('True')
        else:
            visual.append('False')
    return visual

def check_col_name(t,column_info):
    col_name= list()
    for i in t:
        if i in column_info:
            col_name.append('True')
        else:
            col_name.append('False')
    return col_name

def check_numeric(t):
    numeric_val =list()
    for i in t:
        if i.isdigit() :
            numeric_val.append('True')
        else:
            numeric_val.append('False')
    return numeric_val

def check_order(t):
    order_val = list()
    for i in t:
        if i in order_lexicon:
            order_val.append('True')
        else:
            order_val.append('False')
    return order_val

def check_having_clause(t):
    having_val =list()
    for i in t:
        if i in having_lexicon:
            having_val.append('True')
        else:
            having_val.append('False')
    return having_val


# In[9]:


def get_metadata(data_file_loc):
    #file = 'C://Users//himanshu.b//nl_sql_data/nl_sql_sample_data_2.csv'
    file = data_file_loc
    file_type = file.split(".")[-1]
    if file_type == 'csv':
        df = pd.read_csv(file)
    elif file_type == 'xlsx':
        df = pd.read_excel(file)
    elif file_type == 'txt':
        import csv
        with open(file,'r') as f1:
            dialect = csv.Sniffer().sniff(f1.read())
            f1.seek(0)
            df_data= pd.read_csv(f1, delimiter=dialect.delimiter )
    table_name = file.split('//')[-1]
    column_info_original = list(df.columns)
    #print(column_info_original)
    column_info=[i.lower() for i in column_info_original]
    
    column_type = list(df.dtypes)
    col_def =list()
    for i in column_info:
        if len(wordnet.synsets(i))>=1:
            col_def.append(wordnet.synsets(i)[0].definition())
        else:
            col_def.append('null')
    df_metadata = pd.DataFrame({'col_name':column_info_original,'table_name': [table_name]*len(column_info_original),
                                   'col_type':column_type,'col_des': col_def,'alias':''})
    #'col_des':col_def ,
    #print(df_metadata)
    #out = df_metadata.to_json(orient='split')
    #print(out)
    #,'col_description': col_def
    col_type_numeric = df_metadata[df_metadata['col_type']!=object]['col_name'].tolist()
    col_type_numeric = [i.lower() for i in col_type_numeric]
    print(col_type_numeric)
    
    col_type_object = df_metadata[df_metadata['col_type']==object]['col_name'].tolist()
    col_type_object =[i.lower() for i in col_type_object]
    return table_name,column_info,column_type,col_type_numeric,col_type_object, df_metadata


# In[10]:


def get_clean_data(data_file_loc):
    #file = 'C://Users//himanshu.b//nl_sql_data/nl_sql_sample_data_2.csv'
    file= data_file_loc
    df = pd.read_csv(file)
    df_clean_data = pd.DataFrame()

    for i in df.columns[0:]:
        dat = df.loc[:,i]
        if(dat.dtype=='object'):
            dat = dat.str.lower()
            df2 = dat.drop_duplicates()
            df_clean_data = df_clean_data.append(df2)
        else :
            df_clean_data=df_clean_data.append(dat)
    return df_clean_data


# In[11]:


#modeified code
def get_date_info(date_number,date_type,current_date,previous_date):
    current_date= current_date
    previous_date=previous_date
    import datetime
    if (current_date == 'NA') & (previous_date == 'NA'):
        today = datetime.date.today()
    
        if (date_type == 'day') | (date_type == 'days'):
            current_date = today - timedelta(1)
            delta = int(date_number)
            previous_date= today-timedelta(delta)
            #print(current_date)
            #print(previous_date)

        elif (date_type == 'week') | (date_type == 'weeks'):
            present_year,present_week, present_day = today.isocalendar()
            #print(present_year)
            current_week = present_week-1
            if present_day == 7:
                current_date = today
            else:
                current_date = today - timedelta(present_day)

            delta = (int(date_number) *7)-1
            previous_date = current_date - timedelta(delta)
        else:
            current_date = 'NA'
            previous_date = 'NA'
    else:
        return current_date , previous_date
    return current_date, previous_date


# In[12]:


def get_alias(us_input,alias_file_loc):
    ##file = 'C://Users//himanshu.b//nl_sql_data//alias_data.csv'
    file = alias_file_loc
    df_alias = pd.read_csv(file)
    
    
    ##--new code
    df_alia =pd.DataFrame()
    for i in df_alias.columns[0:]:
        
        dat = df_alias.loc[:,i]
        if (dat.dtype == 'object'):
            dat = dat.str.lower()
            df_alia =df_alia.append(dat)
        else:
            df_alia=df_alia.append(dat)
            
    df_alia=df_alia.transpose()
    ##--new code
    df_alias =df_alia
    co_tokens= nltk.word_tokenize(us_input)
    alias_list=list()
    f =list()
    col_alias_user = list()
    for i in co_tokens:
        if i in df_alias['alias'].values:
            alias_list.append(i)
    print(alias_list)
    for i in alias_list:
        idx = df_alias.index[df_alias['alias']==str(i)]
        [f.append(df_alias.iloc[i,:]['col_name']) for i in idx]
        [col_alias_user.append(df_alias.iloc[i,:]['alias']) for i in idx]
        
    n=0
    for x,y in enumerate(co_tokens):
        if y in alias_list:
            co_tokens[x] = f[n]
            n +=1
    us_input = ' '.join(co_tokens)
    return us_input,col_alias_user,dict(zip(f,col_alias_user))


# In[13]:


def get_graph_header(us_input):
    parser = CoreNLPParser(url ='http://localhost:9000')
    core_tokens = list(parser.tokenize(us_input))
    df_new = pd.DataFrame({'ngram': core_tokens})
    pos_tag = [b for a,b in get_pos_tag(core_tokens)]
    df_new1 = pd.DataFrame({'pos-tag':pos_tag})
    df_header = pd.concat([df_new,df_new1],axis=1)
    df_header_final = df_header[df_header['pos-tag'].isin(['IN','NN','NNS','RBR','CD','JJ','WRB','JJR'])]
    graph_header_list = []
    graph_header_list= df_header_final['ngram'].tolist()
    
    print(df_header_final)
    graph_header_list_updated =[]
    for i in graph_header_list:
        if i == spell(i):
            graph_header_list_updated.append(i)
    graph_header=" ".join(graph_header_list_updated)
    graph_header = graph_header.capitalize()
    return graph_header
    


# In[14]:


def build_Dataframe(us_input,data_file_loc):
    #user_input = get_input()
    #word_tokenizer='NA'
    #cleaned_words = cleaning(user_input)
    #print(cleaned_words)
    #word_tokenizer = create_tokenizer(user_input)
    #vocab_size = len(word_tokenizer.word_index) + 1
    #max_length = max_length(user_input)
    #pred = get_predictions(user_input,word_tokenizer)
    #model_output = get_final_output(pred,['greet_hello', 'other_other', 'greet_goodbye'])

    #print(model_output)
    
    
    parser = CoreNLPParser(url ='http://localhost:9000')
    core_tokens = list(parser.tokenize(us_input))
    print(core_tokens)
    #core_tokens = nltk.word_tokenize(user_input)
    bi_grams =  [' '.join(b) for b in get_bigrams(us_input)]
    df = pd.DataFrame({'ngram': core_tokens,'ngram-type':['uni-gram']* len(core_tokens)})
    df1 = pd.DataFrame({'ngram':bi_grams,'ngram-type':['bi_grams']* len(bi_grams)})
    df2 = pd.concat([df,df1],ignore_index=True)
    pos_tag = [b for a,b in get_pos_tag(core_tokens)]
    ner_tag = [b for a,b in get_ner_tag(core_tokens)]
    df3 = pd.DataFrame({'pos-tag':pos_tag, 'ner-tag':ner_tag})
    table_name,column_info,column_type,col_type_numeric,col_type_object, df_metadata = get_metadata(data_file_loc)
    
    
    df_clean_data = get_clean_data(data_file_loc)
    df_clean_data_transposed = df_clean_data.T
    
    flat_list = itertools.chain.from_iterable(df_clean_data_transposed.values.tolist())
    
    df_final = pd.concat([df2,df3],axis=1)
    df_final['initial_lex_value'] = get_initial_lex_value(df_final['ngram'])
    df_final['select_lex_value'] = get_select_lex_value(df_final['ngram'])
    df_final['top_lex_value'] = get_top_lex_value(df_final['ngram'])
    df_final['bottom_lex_value'] = get_bottom_lex_value(df_final['ngram'])
    df_final['aggregate_lex_value'] = get_aggregate_lex_value(df_final['ngram'])
    df_final['operator_lex_value'] = get_operator_lex_value(df_final['ngram'])
    df_final['visual_lex_value'] = get_visual_lex_value(df_final['ngram'])
    df_final['col_name_value'] = check_col_name(df_final['ngram'],column_info)
    df_final['is_numeric_value'] = check_numeric(df_final['ngram'])
    df_final['order_lex_value'] = check_order(df_final['ngram'])
    df_final['having_clause_lex_value'] =check_having_clause(df_final['ngram'])
    df_final['conj_lex_value'] = get_conj_lex_value(df_final['ngram'])
    df_final['date_lex_value'] = get_date_lex_value(df_final['ngram'])
    df_final['is_col_value']= df_final['ngram'].isin(flat_list)
    
    matches = re.findall('(\d{2}[\/ ](\d{2}|January|Jan|February|Feb|March|Mar|April|Apr|May|May|June|Jun|July|Jul|August|Aug|September|Sep|October|Oct|November|Nov|December|Dec)[\/ ]\d{2,4})', us_input)
    date_count=0
    dates = list()
    for match in matches:
        #print(match[0])
        dates.append(match[0])
        date_count= date_count+1
    current_date = 'NA'
    previous_date = 'NA'
    if date_count ==0:
        date_present = 'False'
    elif date_count ==1:
        current_date = dates[0]
        date_present = 'True'
    elif date_count ==2:
        date_present = 'True'
        current_date = dates[1]
        previous_date = dates[0]
    return df_final, df_metadata,df_clean_data_transposed, current_date, previous_date, date_present,us_input


# In[15]:


#us_input= 'show me last 2 weeks region by sales'
#data_file_loc='c:/Users/himanshu.b/nl_sql_data/sample_data_ashish.csv'
#df_fin, df_met,df_cl,cu_date,pre_date,dat_pr,us_input = build_Dataframe(us_input,data_file_loc)
#print(df_fin)


# In[94]:


def build_info(df_final,df_metadata,current_date,previous_date):
    
    #print(df_metadata)
    #incorporate top and bottom code
    top_number='NA'
    order_symbol = 'NA'
    top_present = [i for i in df_final['top_lex_value'] if i == 'True']
    if 'True' in top_present:
        idx= df_final.index[df_final['top_lex_value']=='True'].tolist()
        for i in idx:
            i+=1
        top_number = df_final.iloc[i,:]['ner-tag']
        if top_number == 'NUMBER':
            top_number = df_final.iloc[i,:]['ngram']
            order_symbol = 'desc'
        else:
            top_number = str(1)
            order_symbol = 'desc'
    else:
        top_present = 'False'

   
    #incorporate bottom code with date code 
    
    bottom_number= 'NA'
    date_number = 'NA'
    date_type = 'NA'
    bottom_present = [i for i in df_final['bottom_lex_value'] if i == 'True']
    if 'True' in bottom_present:
        idx = df_final.index[df_final['bottom_lex_value']=='True'].tolist()
        for i in idx:
            i =i
            j=i+1
            bottom_check = df_final.iloc[i,:]['ner-tag']
            if bottom_check =='O':
                if df_final.iloc[j,:]['ner-tag'] == 'NUMBER':
                    bottom_number = df_final.iloc[j,:]['ngram']
                    order_symbol ='asc'
                else:
                    bottom_number = 1
                    order_symbol= 'asc'
            elif bottom_check == 'DATE':
                if df_final.iloc[j,:]['pos-tag'] == 'CD':
                    date_number = df_final.iloc[j,:]['ngram']
                    j=j+1
                    date_type =df_final.iloc[j,:]['ngram']
                else:
                    date_number =1
                    date_type = df_final.iloc[j,:]['ngram']
    else:
        bottom_present ='False'
    
    #incorporate select code 
    select_symbol ='NA'
    select_present = [i for i in df_final['select_lex_value'] if i == 'True']
    if 'True' in select_present:
        select_symbol = '*'
    else:
        select_symbol = 'NA'
            
    
    #incorporate operator code
    operator_value = 0
    operator_symbol = 'NA'
    operator_present = [i for i in df_final['operator_lex_value'] if i == 'True']
    if 'True' in operator_present:
        idx= df_final.index[df_final['operator_lex_value']=='True'].tolist()
        for i in idx:
            i = i
        operator_value = df_final.iloc[i,:]['ngram']
        more_lex = ['more than','greater than','higher than','above']
        less_lex = ['less than','lesser than','lower than']
        equal_lex = ['equal to','equals','as','is','equal']
        if operator_value in more_lex:
            operator_symbol = '>'
        elif operator_value in less_lex:
            operator_symbol = '<'
        elif operator_value in equal_lex:
            operator_symbol = '='
        else:
            operator_symbol = 'NA'
    else:
        operator_present = 'False'
        
    #incorporate aggregate code
    aggregate_value=list()
    aggregate_symbol = 'NA'
    aggregate_present = [i for i in df_final['aggregate_lex_value'] if i =='True']
    if 'True' in aggregate_present:
        idx = df_final.index[df_final['aggregate_lex_value'] == 'True'].tolist()
        for i in idx:
            aggregate_value.append(df_final.iloc[i,:]['ngram'])
        sum_lex = ['sum','summation','volume','amount','by','total']
        count_lex = ['count','number']
        mean_lex = ['average','mean']
        if aggregate_value[0] in sum_lex:
            aggregate_symbol = 'sum'
        elif aggregate_value[0] in count_lex:
            aggregate_symbol = 'count'
        elif aggregate_value[0] in mean_lex:
            aggregate_symbol = 'avg'
        else: 
            aggregate_symbol = 'NA'
    else:
        aggregate_present ='False'
    #print(aggregate_symbol)
    
    #incorporate order code
    order_value =0
    order_present = [i for i in df_final['order_lex_value'] if i == 'True']
    if 'True' in order_present:
        idx = df_final.index[df_final['order_lex_value'] == 'True'].tolist()
        for i in idx:
            i =i
        order_value = df_final.iloc[i,:]['ngram']
        asc_lex = ['ascending','asc']
        desc_lex = ['descending','desc']
        if order_value in asc_lex:
            order_symbol = 'asc'
        elif order_value in desc_lex:
            order_symbol = 'desc'
        else:
            order_symbol = 'NA'
    else:
        order_present = 'False'
    
    #incorporate conjunction code
    conj_value = 0
    conj_symbol = 'NA'
    conj_present = [i for i in df_final['conj_lex_value'] if i == 'True']
    if 'True' in conj_present:
        idx = df_final.index[df_final['conj_lex_value']=='True'].tolist()
        for i in idx:
            i=i
        conj_value = df_final.iloc[i,:]['ngram']
        and_lex = ['and','&']
        or_lex = ['or']
        if conj_value in and_lex:
            conj_symbol ='&'
        elif conj_value in or_lex:
            conj_symbol = '|'
        else:
            conj_symbol = 'NA'
    else:
        conj_present = 'False'
        
    #incorporate the col name in query 
    col_name_query = list()
    col_name_chek = [i for i in df_final['col_name_value'] if i == 'True']
    if 'True' in col_name_chek:
        idx = df_final.index[df_final['col_name_value']== 'True'].tolist()
        for i in idx:
            col_name_query.append(df_final.iloc[i,:]['ngram'])
            
    
    #incorporate column value code
    col_value_query = list()
    col_value_chek = [i for i in df_final['is_col_value'] if i == True]
    if True in col_value_chek:
        idx = df_final.index[df_final['is_col_value']== True].tolist()
        for i in idx:
              col_value_query.append(df_final.iloc[i,:]['ngram'])
    
    #incorporate numeric value code 
    numeric_value = list()
    numeric_value_chek = [i for i in df_final['is_numeric_value'] if i == 'True']
    if 'True' in numeric_value_chek:
        idx = df_final.index[df_final['is_numeric_value']=='True'].tolist()
        for i in idx:
            numeric_value.append(df_final.iloc[i,:]['ngram'])
            
    #incorporate table name code
    tbl_name = df_metadata.loc[0,'table_name']
    
    #incorporate having clause code
    having_value='NA'
    having_present = [i for i in df_final['having_clause_lex_value'] if i == 'True']
    if 'True'  in having_present:
        having_value='having'
    else:
        having_value = 'NA'
            
    
    #incorporate dates
    current_date = current_date
    previous_date=previous_date
    current_date , previous_date = get_date_info(date_number,date_type,current_date,previous_date)
    
    return  top_number, bottom_number,select_symbol ,aggregate_symbol,having_value ,operator_symbol, order_symbol, conj_symbol, col_name_query,col_value_query,numeric_value,tbl_name, current_date, previous_date


# In[85]:


def build_query(us_input,data_file_loc,col_alias_user):
    df_final , df_metadata,df_clean_data_transposed ,current_date, previous_date, date_present,user_input = build_Dataframe(us_input,data_file_loc)
    top_number, bottom_number,select_symbol ,aggregate_symbol,having_value,operator_symbol, order_symbol,conj_symbol,col_name_query,col_value_query,numeric_value,tbl_name, current_date, previous_date= build_info(df_final,df_metadata,current_date,previous_date)
    table_name,column_info,column_type,col_type_numeric,col_type_object, df_metadata=get_metadata(data_file_loc)
    print(top_number,bottom_number,select_symbol,aggregate_symbol,having_value,operator_symbol,order_symbol,conj_symbol,col_name_query,col_value_query,numeric_value,tbl_name,current_date,previous_date)
    print(df_final)
    print("col_type_numeric",col_type_numeric)
    print("col_type_object",col_type_object)
    query = ''
    #print(current_date,previous_date)
    
    #incorporate top queries
    if (top_number != 'NA') & (bottom_number == 'NA') & (conj_symbol == 'NA') & (aggregate_symbol == 'NA'):
        query = 'select'+ ' ' + ','.join(col_name_query) + ' '+ 'from' + ' ' + 'df5'
        
        if (operator_symbol != 'NA') & (conj_symbol == 'NA') & (aggregate_symbol == 'NA'):
            query = query + ' '+ 'where' + ' ' + col_name_query[-1] +' '+ operator_symbol
            if len(col_value_query) !=0:
                #if col_value_query[0] in df_clean_data_transposed[col_name_query[-1]].tolist():
                query = query + "'"+col_value_query[0] + "'" 
                #else:
                   
            elif (len(col_value_query) == 0) & (len(numeric_value)>1) & (current_date == 'NA'):
                query = query +' ' + numeric_value[-1] + ' '+'order by' +' '+ col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+top_number
            
            elif (len(col_value_query) == 0) & (len(numeric_value)> 1) & (current_date != 'NA') & (previous_date != 'NA'):
                query = query  + ' '+ numeric_value[1] + ' '+ 'and date between' + ' ' + "'"+str(previous_date)+"'" +' ' +'and'+ ' ' + "'"+str(current_date)+"'"+' '+'order by' +' '+ col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+top_number
        
        elif (operator_symbol == 'NA') & (conj_symbol == 'NA') & (current_date == 'NA') & (previous_date == 'NA'):
            query = query + ' '+ 'order by' + ' '+col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+str(top_number)
        
        elif (operator_symbol == 'NA') & (conj_symbol == 'NA') & (current_date != 'NA') & (previous_date != 'NA'):
            query = query + ' '+'where' + ' ' + 'date between'+' '+ "'"+str(previous_date)+"'" +' '+ 'and' + ' '+ "'"+str(current_date)+"'"+ ' '+ 'order by' +' ' +col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+str(top_number)
        
        
    #incorporate generic queries 
    
    elif (top_number == 'NA') & (bottom_number == 'NA') & (conj_symbol =='NA') & (aggregate_symbol == 'NA'):
        query = 'select'+' '+','.join(col_name_query)+' '+'from'+' '+ 'df5'
        #print(query)
        
        if (operator_symbol != 'NA') & (conj_symbol == 'NA') & (aggregate_symbol == 'NA'):
            query = query +' '+ 'where'+' '+ col_name_query[-1]+' '+operator_symbol
            
            
            if (len(col_value_query) !=0):
                
                if (col_value_query[0] in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)==0) & (current_date=='NA')& (previous_date=='NA')& (order_symbol=='NA'):
                    #print('hi')
                    query = query + "'"+col_value_query[0] + "'"
                
                elif (col_value_query[0] in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)>=1) & (current_date!='NA')& (previous_date!='NA')& (order_symbol=='NA'):
                    query = query + col_value_query[0]+"'"+' '+ 'and date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"
                    
                elif (col_value_query[0] in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)>=1) & (current_date!='NA')& (previous_date!='NA')& (order_symbol!='NA'):
                    query = query +' '+ 'order by'+' '+ col_name_query[-1]+' '+order_symbol
                
                
                elif (col_value_query[0] not in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)==0) & (current_date=='NA')& (previous_date=='NA')& (order_symbol=='NA'):
                    #print('helloooooooooo')
                    query = query+' '+'('+ ' '+'select'+ ' '+col_name_query[-1]+' '+ 'from'+' '+ 'df5'+' '+'where'+' '+ col_name_query[0] +' '+ '='+' '+ "'"+col_value_query[0]+"'"+')'
                    
                elif (col_value_query[0] not in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)==0) & (current_date=='NA')& (previous_date=='NA')&(order_symbol!='NA'):
                    query = query+' '+'('+ ' '+'select'+ ' '+col_name_query[-1]+' '+ 'from'+' '+ 'df5'+' '+'where'+' '+ col_name_query[0] +' '+ '='+' '+ "'"+col_value_query[0]+"'"+')'+' '+'order by'+' '+col_name_query[-1]+' '+order_symbol
                    
                elif (col_value_query[0] not in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)>=1) & (current_date!='NA')& (previous_date!='NA')&(order_symbol=='NA'):
                    #print('hello')
                    query = query+' '+'('+ ' '+'select'+ ' '+col_name_query[-1]+' '+ 'from'+' '+ 'df5'+' '+'where'+' '+ col_name_query[0] +' '+ '='+' '+ "'"+col_value_query[0]+"'"+')'+' '+'and date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"
                
                
                elif (col_value_query[0] not in df_clean_data_transposed[col_name_query[-1]].tolist()) & (len(numeric_value)>=1) & (current_date!='NA')& (previous_date!='NA')&(order_symbol!='NA'):
                    query = query+' '+'('+ ' '+'select'+ ' '+col_name_query[-1]+' '+ 'from'+' '+ 'df5'+' '+'where'+' '+ col_name_query[0] +' '+ '='+' '+ "'"+col_value_query[0]+"'"+')'+' '+'and date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'order by'+' '+col_name_query[-1]+' '+order_symbol
                     
                
                #else:
                 #elif   
            elif (len(col_value_query) == 0) & (len(numeric_value)>=1) & (current_date == 'NA') & (order_symbol =='NA'):
                query = query +' '+ numeric_value[-1]
            
            elif (len(col_value_query) == 0) & (len(numeric_value)==0) & (current_date == 'NA') & (order_symbol !='NA'):
                query = 'select'+' '+','.join(col_name_query)+' '+'from'+' '+ 'df5'+ ' '+ 'order by'+' '+ col_name_query[-1]+' '+order_symbol
            
            elif (len(col_value_query) == 0) & (len(numeric_value)>=1) & (current_date == 'NA') & (order_symbol !='NA'):
                query = query +' '+ numeric_value[-1]+ ' '+ 'order by'+ ' '+ col_name_query[-1] +' '+ order_symbol
            
            elif (len(col_value_query) == 0) & (len(numeric_value)>=1) & (current_date != 'NA') & (order_symbol !='NA'):
                
                if (previous_date!='NA'):
                    #print('hioioioi')
                    query = query +' '+ numeric_value[0] +' '+ 'and date between'+' '+ "'"+str(previous_date)+"'"+' '+'and' +' '+"'"+str(current_date)+"'"+' '+'order by'+' '+col_name_query[-1]+' '+order_symbol
                else :
                    query = query +' '+ numeric_value[-1] +' '+ 'date ='+' '+ str(current_date)
            #elif ((len(col_value_query) == 0) & (len(numeric_value)>=1) & (current_date != 'NA')):
                
            elif (len(col_value_query) == 0) & (len(numeric_value)>=1) & (current_date != 'NA') & (order_symbol =='NA'):
                if (previous_date!='NA'):
                    query = query +' '+ numeric_value[0] +' '+ 'and date between'+' '+"'"+str(previous_date)+"'"+' '+'and' +' '+"'"+str(current_date)+"'"
                else :
                    query = query +' '+ numeric_value[-1] +' '+ 'date ='+' '+ str(current_date)
                    
    #incorporate bottom code
    
    elif (top_number == 'NA') & (bottom_number != 'NA') & (conj_symbol == 'NA') & (aggregate_symbol == 'NA'):
        query = 'select'+ ' ' + ','.join(col_name_query) + ' '+ 'from' + ' ' + 'df5'    
        if (operator_symbol != 'NA') & (conj_symbol == 'NA') & (aggregate_symbol == 'NA'):
            query = query + ' '+ 'where' + ' ' + col_name_query[-1] +' '+ operator_symbol
            if len(col_value_query) !=0:
                
                #if col_value_query[0] in df_clean_data_transposed[col_name_query[-1]].tolist():
                    query = query + "'"+col_value_query[0] + "'" 
                #else:
                    
            elif (len(col_value_query) == 0) & (len(numeric_value)>1) & (current_date == 'NA'):
                query = query +' ' + numeric_value[-1] + ' '+'order by' +' '+ col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+bottom_number
            
            elif (len(col_value_query) == 0) & (len(numeric_value)> 1) & (current_date != 'NA') & (previous_date != 'NA'):
                query = query  + ' '+ numeric_value[1] + ' '+ 'and date between' + ' ' + "'"+str(previous_date)+"'" +' ' +'and'+ ' ' + "'"+str(current_date)+"'"+' '+'order by' +' '+ col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+bottom_number
             
            #elif (len(col_value_query) == 0) & (len(numeric_value) == 1) & (current_date != 'NA') & (previous_date == 'NA'):
             #   query = query + 
        
        elif (operator_symbol == 'NA') & (conj_symbol == 'NA') & (current_date == 'NA') & (previous_date == 'NA'):
            query = query + ' '+ 'order by' + ' '+col_name_query[-1] + ' ' + order_symbol+' '+'limit'+' '+ str(bottom_number)
        
        elif (operator_symbol == 'NA') & (conj_symbol == 'NA') & (current_date != 'NA') & (previous_date != 'NA'):
            query = query + ' '+'where' + ' ' + 'date between'+' '+ "'"+str(previous_date)+"'" +' '+ 'and' + ' '+ "'"+str(current_date)+"'"+ ' '+ 'order by' +' '+col_name_query[-1] +' '+ order_symbol+' '+'limit'+' '+ str(bottom_number)
            
    
    
    
   
    #incorporate aggregate queries
    elif (top_number == 'NA') & (bottom_number == 'NA') & (conj_symbol == 'NA') & (aggregate_symbol != 'NA')&(current_date=='NA'):
        print('hiiiiiiooo')
        col_aggregation =[]
        #print("col-name_query",col_name_query,"col_type_numeric",col_type_numeric)
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        
        # & (len(col_aggregation==0)):
        if (len(col_name_query)==1) & (len(col_aggregation)==0):
            query = 'select'+' '+"".join(col_name_query)+','+''+str(aggregate_symbol)+'('+"".join(col_name_query)+')'+' '+'as'+' '+ 'count'+' '+'from df5'+' '+'group by'+' '+ "".join(col_name_query)
        
        elif(len(col_name_query)>1):
            print('hiiioooo1111')
            ##--change code
            #query =
            #print(query,col_name_query)
            #-b=col_name_query[0]
            b = " ".join(col_aggregation)
            col_name_query_mod = [x for x in col_name_query if x!=b]
            col_name_query_mod = list(dict.fromkeys(col_name_query_mod))
            #query = 'select'+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')' +' '+'as'+' '+ ",".join(col_aggregation)+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
            #print(query)
            query = 'select'+' '+','.join(col_name_query_mod)+' '+','+ str(aggregate_symbol)+'('+",".join(col_aggregation)+')' +' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
            #print(query)
            if(order_symbol!='NA') & (having_value=='NA'):
                query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol

            elif (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>=1)&(len(col_value_query)==0):
                #print(col_name_query)
                query = query+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+ numeric_value[0]
                if (order_symbol!='NA'):
                    query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol

            elif (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)==0)&(len(col_value_query)>=1):
                query = query+' '+'having'+' '+col_name_query[-1]+' '+operator_symbol+' '+"'"+col_value_query[0]+"'"
                if (order_symbol!='NA'):
                    query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol
        else:
            query= 'select'+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+",".join(col_aggregation)+' '+'from df5'
            
    
    
   


    #incorporating aggregate queries with conjuction 
    elif (top_number == 'NA') & (bottom_number == 'NA') & (conj_symbol != 'NA') & (aggregate_symbol != 'NA')&(current_date=='NA'):
        col_aggregation =[]
        #print("col-name_query",col_name_query,"col_type_numeric",col_type_numeric)
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        
        
        b = " ".join(col_aggregation)
        col_name_query_mod = [x for x in col_name_query if x!=b]
        col_name_query_mod = list(dict.fromkeys(col_name_query_mod))
        query = 'select'+' '+','.join(col_name_query_mod)+' '+','+ str(aggregate_symbol)+'('+",".join(col_aggregation)+')' +' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
        
        if(order_symbol!='NA') & (having_value=='NA'):
            query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol
            
        elif (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>=1)&(len(col_value_query)==0):
            #print(col_name_query)
            query = query+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+ numeric_value[0]
            if (order_symbol!='NA'):
                query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol
            #elif (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)==0)&(len(col_value_query)>=1):
             #   query
                
                
                
                
        
        
     
    #--end of above code
    elif (top_number == 'NA') & (bottom_number == 'NA') & (conj_symbol == 'NA') & (aggregate_symbol != 'NA')&(current_date!='NA'):
        col_aggregation =[]
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        #print('huuuuuu')
        if len(col_name_query)>1 :
            #query = 'select'+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)
            #print(query)
            b=" ".join(col_aggregation)
            col_name_query_mod = [x for x in col_name_query if x!=b]
            col_name_query_mod = list(dict.fromkeys(col_name_query_mod))
            #query = 'select'+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+'group by'+' '+','.join(col_name_query_mod)
            query = 'select'+' '+','.join(col_name_query_mod)+' '+','+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+'group by'+' '+','.join(col_name_query_mod)
            #print(query)
            if(order_symbol!='NA')&(having_value=='NA'):
                #print('huuuuuu')
                query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol

            if (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>=1):
                #print('hiiiiiii')
                query = query+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+numeric_value[-1]
                if (order_symbol!='NA'):
                    #print('hello')
                    query = query+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol
                else:
                    query=query
        else:
            query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[0]+')'+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+'and'+' '+"'"+str(current_date)+"'"
                
    
    
    
    
    
    
    elif (top_number != 'NA') & (bottom_number == 'NA') & (conj_symbol == 'NA') & (aggregate_symbol != 'NA')&(current_date=='NA'):
        
        col_aggregation =[]
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        
        
        #query = 'select'+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)
        print("col_aggregation",col_aggregation)
        b = " ".join(col_aggregation)
        print("b",b)
        #print("before",col_name_query_mod)
        col_name_query_mod = [x for x in col_name_query if x!=b]
        col_name_query_mod = list(dict.fromkeys(col_name_query_mod))
        print("after",col_name_query_mod)
  #####----alias done till here 
        #print('hiuuuuu444555')
        #query = 'select'+' '+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+top_number
        query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+top_number
        print('query')
        if (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>1)&(len(col_value_query)==0):
            #print('hiuuuuu444555')
            query = 'select'+' '+','.join(col_name_query_mod) +' '+','+ str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
            query = query+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+str(numeric_value[-1])+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+top_number
        
        elif (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)==1)&(len(col_value_query)!=0):
            query = 'select'+' '+','.join(col_name_query_mod) +' '+','+ str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
            query = query +' '+'having'+' '+col_name_query[-1]+' '+operator_symbol+' '+"'"+col_value_query[0]+"'"+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+top_number
        
        #elif (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)==1)&(len(col_value_query)!=0):
         #   query = 'select'+' '+','.join(col_name_query_mod) +' '+','+ str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
            
        else:
            query=query
                
    
    
    
    
    
    elif (top_number != 'NA') & (bottom_number == 'NA') & (conj_symbol == 'NA') & (aggregate_symbol != 'NA')&(current_date!='NA'):
        
        col_aggregation =[]
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        
        
        #query= 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'
        b = " ".join(col_aggregation)
        col_name_query_mod = [x for x in col_name_query if x!=b]
        col_name_query_mod = list(dict.fromkeys(col_name_query_mod))
        
        #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+b+' '+order_symbol+' '+'limit'+' '+top_number
        query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+b+' '+order_symbol+' '+'limit'+' '+top_number
        
        if (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>=1):
            #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+ 'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)
            query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+ 'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)
            query = query+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+str(numeric_value[-1])+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+top_number
        else:
            query = query
            
            
    elif (top_number == 'NA') & (bottom_number != 'NA') & (conj_symbol == 'NA') & (aggregate_symbol != 'NA')&(current_date=='NA'):
        
        col_aggregation =[]
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        
        #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'
        b = " ".join(col_aggregation)
        col_name_query_mod = [x for x in col_name_query if x!=b]
        col_name_query_mod =  list(dict.fromkeys(col_name_query_mod))
        
        #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+bottom_number
        query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+bottom_number
        
        if (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>=1):
            #print('hi')
            #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)
            #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+str(numeric_value[0])+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+bottom_number
            query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+str(numeric_value[0])+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+bottom_number
        else:
            query=query
       
    
    
    elif (top_number == 'NA') & (bottom_number != 'NA') & (conj_symbol == 'NA') & (aggregate_symbol != 'NA')&(current_date!='NA'):
        
        col_aggregation =[]
        for i in col_name_query:
                if i in col_type_numeric:
                    col_aggregation.append(i)
        col_aggregation = list(dict.fromkeys(col_aggregation))
        
        
        #query= 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'
        b = " ".join(col_aggregation)
        col_name_query_mod = [x for x in col_name_query if x!=b]
        col_name_query_mod = list(dict.fromkeys(col_name_query_mod))
        
        #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+b+' '+order_symbol+' '+'limit'+' '+top_number
        query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)+' '+'order by'+' '+b+' '+order_symbol+' '+'limit'+' '+bottom_number
        
        if (having_value !='NA')&(operator_symbol!='NA')&(len(numeric_value)>=1):
            #query = 'select'+' '+str(aggregate_symbol)+'('+col_name_query[1]+')'+' '+','+','.join(col_name_query_mod)+' '+'from df5'+' '+ 'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)
            query = 'select'+' '+','.join(col_name_query_mod)+' '+','+str(aggregate_symbol)+'('+",".join(col_aggregation)+')'+' '+'as'+' '+ ",".join(col_aggregation)+' '+'from df5'+' '+ 'where date between'+' '+"'"+str(previous_date)+"'"+' '+ 'and'+' '+"'"+str(current_date)+"'"+' '+'group by'+' '+','.join(col_name_query_mod)
            query = query+' '+'having'+' '+str(aggregate_symbol)+'('+b+')'+' '+operator_symbol+' '+str(numeric_value[-1])+' '+'order by'+' '+str(aggregate_symbol)+'('+b+')'+' '+order_symbol+' '+'limit'+' '+bottom_number
        else:
            query = query
    #print(df_final)
    #graph_text = build_graph_text(top_number, bottom_number,aggregate_symbol,col_name_query,us_input)
    return query,user_input#,graph_text


# In[18]:


#def build_graph_text(top_number, bottom_number,aggregate_symbol,col_name_query,us_input):
 #   if 
    


# In[19]:

model = load_model('C:/dhi_intelligence/AI/model.h24')
model._make_predict_function()


# In[21]:


def load_dataset(filename):
  df = pd.read_csv(filename, encoding = "latin1", names = ["Sentence", "Intent"])
  #print(df.head())
  intent = df["Intent"]
  unique_intent = list(set(intent))
  #print('helooooo')
  print(unique_intent)
 
  sentences = list(df["Sentence"])
  
  return (intent, unique_intent, sentences)
  


intent, unique_intent, sentences = load_dataset("C:/dhi_intelligence/AI/nl_model_data_1.csv")

#print(sentences[:12])



#define stemmer
stemmer = LancasterStemmer()

def cleaning(sentences):
  words = []
  for s in sentences:
    clean = re.sub(r'[^ a-z A-Z 0-9]', " ", s)
    w = word_tokenize(clean)
    #stemming
    words.append([i.lower() for i in w])
    
  return words  

cleaned_words = cleaning(sentences)
#print(len(cleaned_words))
#print(cleaned_words[:3])  
  


def create_tokenizer(words, filters = '!"#$%&()*+,-./:;<=>?@[\]^_`{|}~'):
  token = Tokenizer(filters = filters)
  token.fit_on_texts(words)
  return token

def max_length(words):
  return(len(max(words, key = len)))
  

word_tokenizer = create_tokenizer(cleaned_words)
vocab_size = len(word_tokenizer.word_index) + 1
max_length = max_length(cleaned_words)

#print("Vocab Size = %d and Maximum length = %d" % (vocab_size, max_length))

def encoding_doc(token, words):
  return(token.texts_to_sequences(words))

encoded_doc = encoding_doc(word_tokenizer, cleaned_words)

def padding_doc(encoded_doc, max_length):
  return(pad_sequences(encoded_doc, maxlen = max_length, padding = "post"))

padded_doc = padding_doc(encoded_doc, max_length)
padded_doc[:6]

#print("Shape of padded docs = ",padded_doc.shape)

#tokenizer with filter changed
output_tokenizer = create_tokenizer(unique_intent, filters = '!"#$%&()*+,-/:;<=>?@[\]^`{|}~')
output_tokenizer.word_index
encoded_output = encoding_doc(output_tokenizer, intent)
encoded_output = np.array(encoded_output).reshape(len(encoded_output), 1)
#encoded_output.shape
def one_hot(encode):
  o = OneHotEncoder(sparse = False)
  return(o.fit_transform(encode))
output_one_hot = one_hot(encoded_output)

#output_one_hot.shape
##from sklearn.model_selection import train_test_split
##train_X, val_X, train_Y, val_Y = train_test_split(padded_doc, output_one_hot, shuffle = True, test_size = 0.2)
##print("Shape of train_X = %s and train_Y = %s" % (train_X.shape, train_Y.shape))
##print("Shape of val_X = %s and val_Y = %s" % (val_X.shape, val_Y.shape))

##def create_model(vocab_size, max_length):
    
##    model = Sequential()
##    model.add(Embedding(vocab_size, 128, input_length = max_length, trainable = False))
##    model.add(Bidirectional(LSTM(128)))
#   model.add(LSTM(128))
##    model.add(Dense(32, activation = "relu"))
##    model.add(Dropout(0.5))
##    model.add(Dense(3, activation = "softmax"))
  
##    return model

##model = create_model(vocab_size, max_length)
##model.compile(loss = "categorical_crossentropy", optimizer = "adam", metrics = ["accuracy"])
##model.summary()

##filename = 'model.h15'
##checkpoint = ModelCheckpoint(filename, monitor='val_loss', verbose=1, save_best_only=True, mode='min')

##hist = model.fit(train_X, train_Y, epochs = 100, batch_size = 32, validation_data = (val_X, val_Y), callbacks = [checkpoint])

#model = load_model("model.h15") 

def predictions(text):
  clean = re.sub(r'[^ a-z A-Z 0-9]', " ", text)
  test_word = word_tokenize(clean)
  test_word = [w.lower() for w in test_word]
  test_ls = word_tokenizer.texts_to_sequences(test_word)
  #print(test_word)
  #Check for unknown words
  if [] in test_ls:
    test_ls = list(filter(None, test_ls))
    
  test_ls = np.array(test_ls).reshape(1, len(test_ls))
 
  x = padding_doc(test_ls, max_length)
  
  pred = model.predict_proba(x)
  #print(pred)
  
  return pred


def get_final_output(pred, classes):
  predictions = pred[0]
  #print(classes)
  classes = np.array(classes)
  #print(classes)
  ids = np.argsort(-predictions)
  classes = classes[ids]
  #print(classes)
  predictions = -np.sort(-predictions)
  print(predictions)
  #for i in range(pred.shape[1]):
  #  print("%s has confidence = %s" % (classes[i], (predictions[i])))
  return classes[0]


# In[21]:


def spell_chek(sent):
    ls_sent_updated=[]
    ls_sent= sent.split(" ")
    for i in ls_sent:
        match = re.findall(r'[0-9]+', i)
        if len(match) ==1:
            ls_sent_updated.append(i)
        elif i==spell(i):
            ls_sent_updated.append(spell(i))
        else:
            ls_sent_updated.append(i)
            ls_sent_updated.append(spell(i))
    sent_updated = " ".join(ls_sent_updated)
    #print(sent_updated)
    return sent_updated


# In[22]:


def clean_us_input(us_input,data_file_loc,alias_file_loc):
    us_input = us_input.lower()
    parser = CoreNLPParser(url ='http://localhost:9000')
    core_tokens = list(parser.tokenize(us_input))
    #print(core_tokens)
    file = data_file_loc
    df_data =pd.read_csv(file)
    col_name = list(df_data.columns)
    col_name = [i.lower() for i in col_name]
    
    df_alias = pd.read_csv(alias_file_loc)
    alias_list =df_alias['alias'].tolist()
    alias_list = [str(i).lower() for i in alias_list]
    # plural list
    
    p =[]
    import inflect
    engine = inflect.engine()
    for i in core_tokens:
        p.append(engine.plural(i))
    print(p)
    
    #match us_input to col name 
    #print(core_tokens)
    for i,n in enumerate(p):
        if (n in col_name) |(n in alias_list):
            core_tokens[i] =n
    us_input_updated = " ".join(core_tokens)     
    #print(us_input_updated)
    #print(col_name)
    #print(alias_list)

    #for i,n in enumerate(p):
    #    print(i,n)
    #    if n in alias_list:
    #        us_input_updated_tokens[i]=n
    #us_input_updated = " ".join(us_input_updated_tokens)
    return us_input_updated


# In[23]:


def format_numeric_col(out):
    col_list=[]
    #print(out)
    #print(out.columns)
    for i in out.columns:
        #print(out[0:1][i].dtype)
        if (out[0:1][i].dtype==float) | (out[0:1][i].dtype==int) |(out[0:1][i].dtype== 'int64')|(out[0:1][i].dtype== 'float64'):
            col_list.append(i)
            for i in col_list:
                out[i] = out.apply(lambda x: "{:,}".format(x[i]), axis=1)
            return out


# In[25]:

def df_to_json(data):
    import json
    row=[]
    output_data={}
    output_data["columNames"]=data.columns.tolist()
    for index in range(data.shape[0]):
        d1={}
        d1["rowNumber"]=str(index+1)
        column_values=[]
        for column in data.columns:
            d2={}

            d2["columnName"]=column
            d2["columnValue"]=str(data.loc[index,column])
            column_values.append(d2)
        d1["columnValues"] = column_values
        row.append(d1)
    output_data["row"] = row
    print(json.dumps(output_data))
    return output_data

def frame_json_response(df_metadata):
    import json
    final_response={}
    row_values = []
    for index, row in df_metadata.iterrows():
        rows = {}
        print('Row : ', row['col_name'], row['table_name'], row['col_type'], row['alias'])
        rows["columnName"]=row['col_name']
        rows["dataType"]=str(row['col_type'])
        rows["alias"]=row['alias']
        row_values.append(rows)
    final_response=row_values
    print('Final Response  : ', final_response)
    return final_response

def build_response(us_input,usr_name,data_file_loc,alias_file_loc):
    try:
        # converting to lowercase 
        us_input =  us_input.lower()
        query = ''
        
        #checking for spelling
        us_input = spell_chek(us_input)
        
        #checking for plural and cleaning the user-input
        us_input = clean_us_input(us_input,data_file_loc,alias_file_loc)
        
        pred = predictions(us_input)
        intent = get_final_output(pred, ['greet_hello','greet_thanks','other_other','greet_goodbye'])
        #print(intent)
        graph_header =''
        if intent == 'greet_hello':
            #print('helloooo')
            hi_list =['Hello , How can I help you today','Hi !!, Hope you are having a good day', 'Hello, please tell me how can I help you']
            secure_random = random.SystemRandom()
            out = secure_random.choice(hi_list)
            return out, graph_header,query
        elif intent == 'greet_goodbye':
            bye_list = ['Bye, Have a Good-Day','Good day :)', 'Have a nice day','Enjoy your day']
            secure_random = random.SystemRandom()
            out = secure_random.choice(bye_list)
            #out ='bye'
            #query ='NA'
            return out, graph_header,query
        elif intent == 'greet_thanks':
            thanks_list =['Glad that I could help. I can help you with more questions.','No problem!! Let me know if you need more help','My pleasure, Would you like to know anything else?']
            secure_random= random.SystemRandom()
            out = secure_random.choice(thanks_list)
            return out , graph_header,query
        elif intent == 'other_other':
            #genrating graph header
            graph_header= get_graph_header(us_input)
            #print(graph_header)
            us_input,col_alias_user,col_dict = get_alias(us_input,alias_file_loc)
            #print(us_input)
            print(col_dict)
            query, user_input= build_query(us_input,data_file_loc,col_alias_user)
            #print(query)
            #query = 'select region,sum(bookings) as bookings from df5 where sub_theater = EMEA group by region'
            df5 = load_data_file(data_file_loc)
            #print(df5.head)
            #print(col_alias_user)
            #file=data_file
            #$file = 'C://Users//himanshu.b//nl_sql_data/nl_sql_sample_data_2.csv'
            #df5 = pd.read_csv(file)
            query =str(query)
            print(query)
            out = ps.sqldf(query,locals())
            
            #rounding off for numeric columns 
            out = out.round(2)
            
            #replacing column names with user column names
            col_dict = {k:v for k,v in col_dict.items()}
            out_col=list()
            for i in map(col_dict.get,out.columns,out.columns):
                out_col.append(i.capitalize())
            out.columns = out_col

            image_path = out_put(out, usr_name)
            print('Image Path ===== : ', image_path)

            #Call to graph reseanor
            respone_from_gr = preparing_VIZ_output_df(out)

            print('Response from graph Reasonser : ', respone_from_gr)

            #formatting the numeric columns with "," after three digits
            out = format_numeric_col(out)
            #print("out",out)
            #subs = 
            #for i in df_col:
            #    if i in col_dict.keys():
            #        df_col[i] = col_dict
            #print(out)
            #out = out.to_json(orient = 'split')
            #print(query)
            #return out,graph_header,query
            tabular_data = df_to_json(out)
            tabular_data["graphTypes"]=respone_from_gr
            print("what is df :", out)
            print("Tabular data", tabular_data)
            return jsonify(
                tabularData=tabular_data,
                title=graph_header,
                generatedSqlQuery=query,
                textMessage='',
                imageUrl=image_path,
                status='success'
            )

            #out, query = build_response(us_input)
        else :
            out = 'could not understand'
            #query = 'NA'
            #return out, graph_header,query
            return jsonify(
                title=graph_header,
                generatedSqlQuery=query,
                textMessage=out,
                imageUrl='',
                status='error'
            )
    except Exception as error:        
        print(error)
        err = "Sorry!, I could not understand. Could you please rephrase your question?"
        #return err
        #return query, user_input, out
        return jsonify(
            # title=graph_header,
            # generatedSqlQuery=query,
            textMessage=err,
            imageUrl='',
            status='error'
        )


# In[99]:


#us_input=input("Hello this is Dhi. would love to help you")
#data_file_loc = 'C://Users/himanshu.b/nl_sql_data/data/dell_sample_data_short.csv'
#alias_file_loc = 'C://Users//himanshu.b//nl_sql_data/data/dell_sample_data_short_alias.csv'
#out,graph_header,query = build_response(us_input,data_file_loc,alias_file_loc)
#print(graph_header)
#print(out)

#outi = out.to_json(orient='split')
#print(outi)
##us_input =input()
##pred = predictions(us_input)
##intent = get_final_output(pred, unique_intent)
##if intent == 'greet_hello':
##    print('hi')


##elif intent == 'greet_goodbye':
##    print('bye')a

##elif intent == 'other_other':
##    out, query = build_response(us_input)


##else :
##    print('could not understand')show me sales by regionshow me region by sales having region as france
##print(intent)
##print(out)


# In[100]:


#if __name__ == "__main__":
    
#    print('Bot is running. please ask question')
#    a=0
#    if (a<2):
#        while True:
#            a=+1
#us_input= input()
#out,query = build_response(us_input)
#print(query)
#print(out)

#    else :
#           print("connection failed. please try again")


# In[ ]:


#file = 'C://Users//himanshu.b//nl_sql_data//nl_table.csv'
#df5 = pd.read_csv(file)
#df5


# In[ ]:


#file = 'C://Users//himanshu.b//nl_sql_data//nl_table.csv'
#df5 = pd.read_csv(file)
#df5
#query = "select sum(sales),grain from df5 group by grain "
#ps.sqldf(query,locals())

