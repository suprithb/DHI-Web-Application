#!/usr/bin/env python
# coding: utf-8

# In[2]:


import pandas as pd
import datetime
import numpy as np
import os
import io
import random
from array import *
import re


# In[1]:



#taking in the desired columns from output dataframe
def Max_Length_Text_col(Input):
    Return_Variable = 0
    for column_name, Series in Input.items():
        if Return_Variable < len(Series):
            Return_Variable = len(Series) 
    return Return_Variable   
def preparing_VIZ_output_df(dff):
    Graph_df = pd.read_excel('C:/Server\DHI/dhi_intelligence/AI_V3/VIZ_EXCEL_DHI.xlsx')
    df=dff
    length = 0
    df_numeric=  df._get_numeric_data() 
    df_categorical= df.select_dtypes(exclude=[np.datetime64,np.timedelta64,np.number])
    df_date = df.select_dtypes(include=[np.datetime64,np.timedelta64])
    if df_date.empty:
        i=0
        scan_row = dff.iloc[:1]
        scan_row.reset_index(inplace = True)
        ckeck_list=scan_row.values.tolist()[0]    
        dateRegex = re.compile(r"(^(0*[1-9]|[12][0-9]|3[01])[- \/.]((0*[1-9]|1[012])|([Jj]an|[Jj]anuary|[Ff]eb|[Ff]ebruary|[Mm]ar|[Mm]arch|[Aa]pr|[Aa]pril|[Mm]ay|[Jj]un|[Jj]une|[Jj]ul|[Jj]uly|[Aa]ug|[Aa]ugust|[Ss]ep|[Ss]eptember|[Oo]ct|[Oo]ctober|[Nn]ov|[Nn]ovember|[Dd]ec|[Dd]ecember))[- \/.]\d{2,4}$)|(^((0*[1-9]|1[012])|([Jj]an|[Jj]anuary|[Ff]eb|[Ff]ebruary|[Mm]ar|[Mm]arch|[Aa]pr|[Aa]pril|[Mm]ay|[Jj]un|[Jj]une|[Jj]ul|[Jj]uly|[Aa]ug|[Aa]ugust|[Ss]ep|[Ss]eptember|[Oo]ct|[Oo]ctober|[Nn]ov|[Nn]ovember|[Dd]ec|[Dd]ecember))[- \/.](0*[1-9]|[12][0-9]|3[01])[- \/.]\d{2,4}$)") 
        for element in ckeck_list:
            mo = dateRegex.match(str(element))
            if mo is not None:
                length = length + 1
        scan_row = df_categorical.iloc[:1]
        scan_row.reset_index(inplace = True)
        ckeck_list=scan_row.values.tolist()[0]
        for element in ckeck_list:
            mo = dateRegex.match(str(element))
            if mo is not None:
                i=i+1
                del df_categorical[df_categorical.columns[i-1]]
        output=Graph_df[(Graph_df['Continuous'] == len(df_numeric.columns)) & (Graph_df['Date_Time'] == length ) & (Graph_df['Categorical'] == len(df_categorical.columns))]
        print("if")
        print(output)
    else:
        output=Graph_df[(Graph_df['Continuous'] == len(df_numeric.columns)) & (Graph_df['Date_Time'] == len(df_date.columns)) & (Graph_df['Categorical'] == len(df_categorical.columns))]
        print("else")
        print(output)
    Col_count = df_categorical.columns
    Length_list = []
    Flag = 0
    required = 0
    if not df_categorical.empty:
        for Col_name in Col_count:
            Length_list.append(len(df[Col_name].unique()))
    
            Array_Length_Text_col = output['Length_Text_col']
            Array_Length_Text_col = Array_Length_Text_col.reset_index()['Length_Text_col']
    
        Max_Len = Max_Length_Text_col(df_categorical)
        for element in Array_Length_Text_col:
            condition = str(Max_Len) + str(element)
            print("Array_Length_Text_col:" + condition)
            if eval(condition):
                Flag = str(element)
                break
            else:
                Flag = str(element)
    
        no_categories2 = Length_list[0]
        Array = []
        Array = output['no_categories2']
        Array = Array.reset_index()['no_categories2']
       
        for i in range(len(Array)):
            if len(str(Array[i]).split()) > 1:
                Condition = str(no_categories2)+" " + str(Array[i])
                if eval(eval('Condition')):
                    required = Array[i] 
                    break
            else:
                Condition = str(no_categories2) +' == ' + str(Array[i])
                if eval(eval('Condition')):
                    required = Array[i] 
                    break 
        output = (output['Graph_Type'][(output['no_categories2'] == required) & (output['Length_Text_col'] == Flag)])
        Graph_List = []
        for graphs in output:
            Graph_List.append(graphs)
        return Graph_List 
    output = output['Graph_Type']
    Graph_List = []
    for graphs in output:
        Graph_List.append(graphs)
    return Graph_List
# preparing_VIZ_output_df(Dff)


# In[ ]:


# %debug

