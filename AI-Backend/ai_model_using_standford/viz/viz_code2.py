
import pandas as pd
#from pandas.core.common import _asarray_tuple
import matplotlib.pyplot as plt
import matplotlib.pyplot as plt; plt.rcdefaults()
import numpy as np; np.random.seed(0)
import seaborn as sns; sns.set()
import ipywidgets as widgets
import os
import io

#df.reset_index(inplace=True, drop = True) 

#Setting current working directory
os.chdir('C://Users//rohit.chaurasiya//Desktop//ChatBot Work//viz')
path="C://Users//rohit.chaurasiya//Desktop//ChatBot Work//viz"
#"C:\Viay\Chatbot\New folder\Dummy Data and Logic"
#df=pd.read_excel("C:/Vijay/Chatbot/New folder/Dummy Data and Logic/scatter.xlsx")
df=pd.read_excel("scatter.xlsx") #Heat_Map scatter
#df=pd.read_csv("scatter.csv", encoding='latin-1')
vz_cond=pd.read_excel("Condition_Viz_selection.xlsx")
df_numeric=df._get_numeric_data()
df_categorical=df.select_dtypes(include='object')
df_date=df.select_dtypes(include='datetime')
   
#################################################################################################################### 
    #Histogram
def histogram(df):

    x = df[df_numeric.columns[0]]
    plt.hist(x, bins=10)
    plt.ylabel('No of times')
    plt.xlabel('Data')
    #plt.show()
    image_path=os.path.join(path,"histogram.png")
    plt.savefig(image_path)
    #plt.savefig(path + 'histogram.png')

#Bar Chart
def bar_chart_hz(df):    
    objects = df[df_categorical.columns[0]]
    y_pos = np.arange(len(objects))
    performance = df[df_numeric.columns[0]]
    plt.barh(y_pos, performance, align='center', alpha=0.5)
    plt.yticks(y_pos, objects)
    #plt.show()
    image_path=os.path.join(path,"bar_chart_hz.png")
    plt.savefig(image_path)
    
def bar_chart_vr(df):    
    objects = df[df_categorical.columns[0]]
    y_pos = np.arange(len(objects))
    performance = df[df_numeric.columns[0]]
    plt.bar(y_pos, performance, align='center', alpha=0.5)
    plt.xticks(y_pos, objects)
    #plt.show()
    image_path=os.path.join(path,"bar_chart_vr.png")
    plt.savefig(image_path)

#Stack Bar Chart

#Scatter Plot
def scatter(df):
    plt.scatter(df[df_numeric.columns[1]],df[df_numeric.columns[0]])
    plt.ylabel(df.columns[0])
    plt.xlabel(df.columns[1])
    image_path=os.path.join(path,"scatter.png")
    plt.savefig(image_path)
    #plt.savefig(path + 'scatter.png')
#    bytes_image = io.BytesIO()
#    plt.savefig(bytes_image, format='png')
#    bytes_image.seek(0)
#    return bytes_image
    #plt.show()
    #plt.savefig('scatter.png')

#Line chart
def Line_graph(df):
    #df.set_index(df_date.columns[0]).plot()
    plt.plot(df[df_date.columns[0]],df[df_numeric.columns])
    plt.xlabel(df_date.columns[0])
    #plt.show()
    image_path=os.path.join(path,"Line_graph.png")
    plt.savefig(image_path)
    #plt.savefig(path + 'Line_graph.png')

#bubble chart with size as dimension
def bubble_three(df):

    x = df[df_numeric.columns[0]]
    y = df[df_numeric.columns[1]]
    N = df[df_numeric.columns[2]]*100
    
    #colors= df[df_categorical.columns[2]]

    #area = (30 * np.random.rand(N))**2  # 0 to 15 point radii

    plt.scatter(x, y, s=N, alpha=0.5)
    #plt.show()
    image_path=os.path.join(path,"bubble_three.png")
    plt.savefig(image_path)
    #plt.savefig(path + 'bubble_three.png')
#bubble chart with size and color as dimension
def bubble_four(df):

    x = df[df_numeric.columns[0]]
    y = df[df_numeric.columns[1]]
    N = df[df_numeric.columns[2]]
    
    colors=  list(df[df_categorical.columns[0]])  
    #area = (30 * np.random.rand(N))**2  # 0 to 15 point radii
    plt.scatter(x, y, s=N, c =colors, alpha=0.5)
    #plt.show()
    image_path=os.path.join(path,"bubble_four.png")
    plt.savefig(image_path)
    #plt.savefig(path + 'bubble_four.png')
#Heat map
def Heat_map(df):
     # plot heatmap
    ax = sns.heatmap(df.set_index(df_categorical.columns[0]))
    # turn the axis label
    for item in ax.get_yticklabels():
        item.set_rotation(0)

    for item in ax.get_xticklabels():
        item.set_rotation(90)
    # save figure
    #plt.show()
    image_path=os.path.join(path,"Heat_map.png")
    plt.savefig(image_path)
    #plt.savefig(path + 'Heat_map.png')

def scatter_plot(df): 
    # Use the 'hue' argument to provide a factor variable
    sns.lmplot( x=df_numeric.columns[0], y=df_numeric.columns[1], data=df, fit_reg=False, hue=df_categorical.columns[0], legend=False)

    # Move the legend to an empty part of the plot
    plt.legend(loc='best') 
    

    ###########################################################################################################
    
def out_put(df):
 
    #categorised data according to type
    length_cat =[0]*len(df_categorical.columns)
    for x in range(len(df_categorical.columns)-1):
        length_cat[x] = (len(df_categorical.iloc[:,x].unique()))

    cont_columns=len(df_numeric.columns)
    cat_columns=len(df_categorical.columns)
    dt_columns=len(df_date.columns)

    #Continuous	Date_Time	Categorical	no_categories	Length_Text_col	Graph_Type

    #vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
    #vz1=vz1['Graph_Type']
    #vz2=eval((vz1.to_string(index=False)))
        
    #th=5
    
    #No measure column
    if (cont_columns == 0 and cat_columns == 0): print('No measure to plot!')

    #One continuos data column -> Histogram
    elif (cont_columns == 1 and cat_columns == 0 and dt_columns == 0): 
        vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #histogram(df)
    
    #two continuous  -> Scatter plot
    elif (cont_columns == 2 and cat_columns == 0 and dt_columns == 0): 
        vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #return scatter(df)
    
    #three continuous  -> bubble plot
    elif (cont_columns == 3 and cat_columns == 0 and dt_columns == 0): 
        vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #bubble_three(df)
    
    #three continuous one categorical -> bubble plot
    elif (cont_columns == 3 and cat_columns == 1 and dt_columns == 0): 
        vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #bubble_four(df)
        
    #One continuos data column and one datetime -> line chart    
    elif (cont_columns >= 1 and cat_columns == 0 and dt_columns == 1):
        vz1=vz_cond[(vz_cond['Continuous'].between(1,cont_columns)) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #Line_graph(df)
        
    #more than three continuous  -> heat map
    elif (cont_columns >= 4 and cat_columns == 1 and dt_columns == 0): 
        vz1=vz_cond[(vz_cond['Continuous'].between(4,cont_columns)) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #Heat_map(df)
    
    #two continuous and one categorical -> Scatter plot - with color for one category (min number of unique category)
    elif (cont_columns == 2 and cat_columns == 1 and dt_columns == 0): 
        vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        #scatter_plot(df)

    #One continuos data column and one categorical data column -> Column chart    
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 0 and len(df_categorical.iloc[:,0].unique()) <= 5): 
        vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) &
                        (vz_cond['Date_Time'] == dt_columns ) & (vz_cond['no_categories'].between(1,4)) & (vz_cond['Length_Text_col'] == 0)]
        vz1=vz1['Graph_Type']
        vz2=eval((vz1.to_string(index=False)))
        return vz2(df)
        
    
    #th=5
    #One continuos data column and one categorical data column and# categories > threshold -> Barchart chart    
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 0 and len(df_categorical.iloc[:,0].unique()) > 5 ): #th
        if df[df_categorical.columns[0]].str.len().max() <= 5:
            vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) &
                        (vz_cond['Date_Time'] == dt_columns ) & (vz_cond['no_categories'].between(1,5)) & (vz_cond['Length_Text_col'].between(1,5))]
            vz1=vz1['Graph_Type']
            vz2=eval((vz1.to_string(index=False)))
            return vz2(df)
            #bar_chart_vr(df)
            
        else:
            vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) 
            & (vz_cond['no_categories'].between(6,len(df_categorical.iloc[:,0].unique()))) & (vz_cond['Length_Text_col'].between(6,df[df_categorical.columns[0]].str.len().max())) ]
            vz1=vz1['Graph_Type']
            vz2=eval((vz1.to_string(index=False)))
            return vz2(df)
            
            #bar_chart_hz(df)

    #One continuos data column and one datetime, one categorical and # categories <= threshold -> Stacked column chart    
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 1 and len(df_categorical.iloc[:,0].unique()) <= th): 
        print('Stacked Column Chart')

    #One continuos data column and one datetime, one categorical and # categories > threshold -> Stacked area chart    
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 1 and len(df_categorical.iloc[:,0].unique()) > th): 
        print('Stacked Area Chart')

    #two categorical  -> column chart (Choose the column with less categories as dimesion and other column as Count measure)     
    elif (cont_columns == 0 and cat_columns == 2 and dt_columns == 0): 
        print('Column Chart')


    #three continuous and two categorical -> Bubble chart
    elif (cont_columns == 3 and cat_columns == 1 and dt_columns == 0): 
        print('Bubble Chart')



    #one continuous two categorical -> heat map
    elif (cont_columns >= 1 and cat_columns == 2 and dt_columns == 0): 
        print('Heat Chart')

    else: print('Unable to find the right graph!') 

#print("generating heat map")
out_put(df)
