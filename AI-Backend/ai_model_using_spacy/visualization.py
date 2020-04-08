import pandas as pd
# from pandas.core.common import _asarray_tuple
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import datetime

plt.rcdefaults()
import numpy as np

np.random.seed(0)
import seaborn as sns

sns.set()
import ipywidgets as widgets
import os
import io
import random

from matplotlib import colors as mcolors
c1 = mcolors.CSS4_COLORS
colour_plan=list(c1.keys())
unwanted = {'white','whitesmoke','lightblue', 'lightcoral',
'lightcyan', 'lightgoldenrodyellow','lightgray','lightgreen', 'lightgrey', 'lightpink', 'lightsalmon',
'lightseagreen', 'lightskyblue', 'lightslategray', 'lightslategrey', 'lightsteelblue', 'lightyellow','beige','aqua',
'mediumaquamarine', 'mediumblue',  'mediumorchid',  'mediumpurple',  'mediumseagreen',  'mediumslateblue',
'mediumspringgreen',  'mediumturquoise',  'mediumvioletred','seagreen',  'seashell','steelblue','navy',
'sienna',  'silver',  'skyblue','aliceblue', 'antiquewhite','azure',  'bisque','blanchedalmond','palegreen','cornsilk',
'chocolate','cornflowerblue','aquamarine',}
colour_list = [e for e in colour_plan if e not in unwanted]


usr_name="a"

# df.reset_index(inplace=True, drop = True)

# Setting current working directory
os.chdir('C:/Server/DHI/dhi_intelligence/AI_V2/viz')
path = 'C:/Server/DHI/dhi_intelligence/AI_V2/viz'

# BASE_DIR = os.path.dirname(os.path.abspath(__file__))
# path = os.path.join(BASE_DIR, 'viz')
# print("Image files path is: ", path)
# "C:\Viay\Chatbot\New folder\Dummy Data and Logic"
# df=pd.read_excel("C:/Vijay/Chatbot/New folder/Dummy Data and Logic/scatter.xlsx")
#df = pd.read_excel("Bar.xlsx")  # Heat_Map scatter Hist Bar Line_Chart Bubble_four
#df=pd.read_csv("scatter.csv", encoding='latin-1')

vz_cond = pd.read_excel("Condition_Viz_selection.xlsx")


def incializedf(dff):
    df=dff
    df_numeric=  df._get_numeric_data()
    df_categorical= df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    return True

#df=None

#df_numeric = None
#df_categorical = None
#df_date = None

def getcurrenttime():
    now = datetime.datetime.now()
    print("Current date and time : ")
    now=str(now.strftime("%Y_%m_%d_%H_%M_%S"))
    print("Current date and time : ",now)
    return now

####################################################################################################################
# Histogram
def histogram(df,usr_name):
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    plt.tight_layout()
    sns.distplot(df[df_numeric.columns[0]], bins=10,rug=True,kde=True,color="blue")
    plt.ylabel('No of times')
    #plt.xlabel('Data points')
    #plt.grid(False)
    # plt.show()
    time_now = getcurrenttime()
    image_path = os.path.join(path, usr_name+ "_histogram"+"_"+time_now+".jpg")
    plt.savefig(image_path, bbox_inches='tight')
    plt.clf()
    return image_path
    # plt.savefig(path + 'histogram.png')
#df_numeric = df._get_numeric_data()
#df_categorical = df.select_dtypes(include='object')
#df_date = df.select_dtypes(include='datetime')
#sns.distplot(df[df_numeric.columns[0]], bins=10,rug=True,kde=True)
#plt.show()


# Horizontal Bar Plot
# Warning: Make sure Categories coming as DataFrame are unique(no repetation)
#          else, the plot may show unwanted lines on top of the plot
def bar_chart_hz(df,usr_name):
    print("Plotting BAR Horizontal Graph")
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    df=df.sort_values(df_numeric.columns[0])

    df = df[[df_numeric.columns[0], df_categorical.columns[0]]]
    groupedvalues=df.groupby(df.columns[1]).sum().reset_index()
    groupedvalues = groupedvalues[[df_numeric.columns[0], df_categorical.columns[0]]]
    
    sns.set(style="whitegrid")
    # clrs = 'Greys'
    clrs = ['yellowgreen']
    time_now=getcurrenttime()
    image_path = os.path.join(path, usr_name+ "_bar_chart_hz"+"_"+time_now+".jpg")
    # plt.xlim(df_numeric[df_numeric.columns[0]].min(), df_numeric[df_numeric.columns[0]].max()*1.1)
    # print("The X limit is from {} to {}".format(df_numeric[df_numeric.columns[0]].min(), df_numeric[df_numeric.columns[0]].max()*1.1))
    plt.xticks(rotation=30)
    plt.tight_layout()
    plt.tick_params(labelsize=14)
    g = sns.barplot(x=groupedvalues.columns[0], y=groupedvalues.columns[1], orient="h",data=groupedvalues,linewidth=2.5, palette=clrs)
    if groupedvalues[groupedvalues.columns[0]].max() >= 1000 and groupedvalues[groupedvalues.columns[0]].max() < 10000000:
        g.xaxis.set_major_formatter(ticker.FuncFormatter(lambda x, pos: '{:}'.format(int(x/1000)) + 'K'))
    elif groupedvalues[groupedvalues.columns[0]].max() >= 10000000:
        g.xaxis.set_major_formatter(ticker.FuncFormatter(lambda x, pos: '{:}'.format(int(x/10000000)) + 'M'))
    plt.savefig(image_path,transparent=True, bbox_inches='tight')
    plt.clf()
    print("Image Path is: ", image_path)
    return image_path

# Vertical Bar Plot
# Warning: Make sure Categories coming as DataFrame are unique(no repetation)
#          else, the plot may show unwanted lines on top of the plot
def bar_chart_vr(df,usr_name):
    print("Plotting BAR Vertical Graph")
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    df=df.sort_values(df_numeric.columns[0])

    df = df[[df_numeric.columns[0], df_categorical.columns[0]]]
    groupedvalues=df.groupby(df.columns[1]).sum().reset_index()
    groupedvalues = groupedvalues[[df_numeric.columns[0], df_categorical.columns[0]]]
    
    sns.set(style="whitegrid")
    # clrs = 'Greys'
    clrs = ['yellowgreen']
    time_now = getcurrenttime()
    image_path = os.path.join(path, usr_name+"_bar_chart_vr"+"_"+time_now+".jpg")
    plt.xticks(rotation=30)
    plt.tight_layout()
    plt.tick_params(labelsize=14)
    # plt.xlim(df_numeric.values.min(), df_numeric.values.max()*1.1)
    # print("The X limit is from {} to {}".format(df_numeric[df_numeric.columns[0]].min(), df_numeric[df_numeric.columns[0]].max()*1.1))
    g=sns.barplot(x=groupedvalues.columns[1],y=groupedvalues.columns[0], orient="v", data=groupedvalues, palette=clrs)
    if groupedvalues[groupedvalues.columns[0]].max() >= 1000 and groupedvalues[groupedvalues.columns[0]].max() < 10000000:
        g.yaxis.set_major_formatter(ticker.FuncFormatter(lambda y, pos: '{:}'.format(int(y/1000)) + 'K'))
    elif groupedvalues[groupedvalues.columns[0]].max() >= 10000000:
        g.yaxis.set_major_formatter(ticker.FuncFormatter(lambda y, pos: '{:}'.format(int(y/10000000)) + 'M'))
    plt.savefig(image_path,transparent=True, bbox_inches='tight')
    plt.clf()
    print("Image Path is: ", image_path)
    return image_path

# Scatter Plot
def scatter(df,usr_name):
    sns.set_style("darkgrid")
    #sns.despine()
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    cmap = sns.cubehelix_palette(dark=.20, light=.1, as_cmap=True)
    plt.tight_layout()
    #with sns.axes_style("darkgrid"):
    sns.scatterplot(x=df[df_numeric.columns[1]], y=df[df_numeric.columns[0]],s=(50,150),legend="full",
                    palette=cmap,data=df)
    #plt.show()
    #plt.grid(False)
    time_now = getcurrenttime()
    image_path = os.path.join(path, usr_name+ "_scatter"+"_"+time_now+".jpg")
    plt.savefig(image_path, bbox_inches='tight')
    plt.clf()
    return image_path
    #plt.show()

# Line chart
def Line_graph(df,usr_name):
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    plt.tight_layout()
    sns.lineplot(x=df_date.columns[0], y=df_numeric.columns[0],markers=True, dashes=True, data=df,color="coral", label=df_numeric.columns[0])
    sns.lineplot(x=df_date.columns[0], y=df_numeric.columns[1],markers=True, dashes=True, data=df,color="blue", label=df_numeric.columns[1])
    plt.ylabel("")
    #plt.legend()
    time_now = getcurrenttime()
    image_path = os.path.join(path,usr_name + "_Line_graph"+"_"+time_now+".jpg")
    plt.savefig(image_path, bbox_inches='tight')
    plt.clf()
    return image_path
    # plt.savefig(path + 'Line_graph.png')

#sns.lineplot(x=df_date.columns[0], y=df_numeric.columns[0],markers=True, dashes=True, data=df,color="coral", label=df_numeric.columns[0],legend="full")
#sns.lineplot(x=df_date.columns[0], y=df_numeric.columns[1],markers=True, dashes=True, data=df,color="blue", label=df_numeric.columns[1],legend="full")
#plt.ylabel("")
#plt.show()


# bubble chart with size as dimension
def bubble_three(df,usr_name):
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    x = df[df_numeric.columns[0]]
    y = df[df_numeric.columns[1]]
    N = df[df_numeric.columns[2]] * 100

    # colors= df[df_categorical.columns[2]]

    # area = (30 * np.random.rand(N))**2  # 0 to 15 point radii
    plt.tight_layout()
    plt.scatter(x, y, s=N, alpha=0.5)
    # plt.show()
    time_now = getcurrenttime()
    image_path = os.path.join(path,usr_name+ "_bubble_three"+"_"+time_now+".jpg")
    plt.savefig(image_path, bbox_inches='tight')
    plt.clf()
    return image_path
    # plt.savefig(path + 'bubble_three.png')


# bubble chart with size and color as dimension
def bubble_four(df,usr_name):
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    x = df[df_numeric.columns[0]]
    y = df[df_numeric.columns[1]]
    N = df[df_numeric.columns[2]]
    plt.tight_layout()
    colors = list(df[df_categorical.columns[0]])
    # area = (30 * np.random.rand(N))**2  # 0 to 15 point radii
    plt.scatter(x, y, s=N*1000, c=["r","g","b"], alpha=0.5)
    #plt.show()
    time_now = getcurrenttime()
    image_path = os.path.join(path,usr_name + "_bubble_four"+"_"+time_now+".jpg")
    plt.savefig(image_path,transparent=True, bbox_inches='tight')
    plt.clf()
    return image_path
    # plt.savefig(path + 'bubble_four.png')
#import matplotlib.colors as mcolors    
#import itertools
#cmap = plt.cm.Spectral
#colors = itertools.cycle(["r", "b", "g"])
#colors = df[df_categorical.columns[0]]
#df_numeric = df._get_numeric_data()
#df_categorical = df.select_dtypes(include='object')
#df_date = df.select_dtypes(include='datetime')
#x = df[df_numeric.columns[0]]
#y = df[df_numeric.columns[1]]
#N = df[df_numeric.columns[2]]
#plt.scatter(x, y, s=N*1000, alpha=0.5, c=["r"])
#plt.show()



# Heat map
def Heat_map(df,usr_name):
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    plt.tight_layout()
    # plot heatmap
    ax = sns.heatmap(df.set_index(df_categorical.columns[0]))
    # turn the axis label
    for item in ax.get_yticklabels():
        item.set_rotation(0)

    for item in ax.get_xticklabels():
        item.set_rotation(90)
    # save figure
    # plt.show()
    time_now = getcurrenttime()
    image_path = os.path.join(path,usr_name+ "_Heat_map"+"_"+time_now+".jpg")
    plt.savefig(image_path, bbox_inches='tight')
    plt.clf()
    return image_path
    # plt.savefig(path + 'Heat_map.png')


def scatter_plot(df,usr_name):
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')
    plt.tight_layout()
    # Use the 'hue' argument to provide a factor variable
    sns.lmplot(x=df_numeric.columns[0], y=df_numeric.columns[1], data=df, fit_reg=False, hue=df_categorical.columns[0],
            legend=False)

    # Move the legend to an empty part of the plot
    plt.legend(loc='best')
    plt.clf()

    ###########################################################################################################


def out_put(df,usr_name):
    print("Called out_put function..")
    df_numeric = df._get_numeric_data()
    df_categorical = df.select_dtypes(include='object')
    df_date = df.select_dtypes(include='datetime')

    # categorised data according to type
    length_cat = [0] * len(df_categorical.columns)
    for x in range(len(df_categorical.columns) - 1):
        length_cat[x] = (len(df_categorical.iloc[:, x].unique()))

    cont_columns = len(df_numeric.columns)
    cat_columns = len(df_categorical.columns)
    dt_columns = len(df_date.columns)
    #if (cat_columns>0):
    #    n=len(df_categorical.iloc[:, 0].unique())
    #    colour_list = [random.choice(colour_list) for i in range(n)]
    #else:
    #    colour_list =["blue"]  
    #colors=np.random.rand(n,n)
    # Continuous	Date_Time	Categorical	no_categories	Length_Text_col	Graph_Type

    # vz1=vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns ) & (vz_cond['Date_Time'] == dt_columns ) ]
    # vz1=vz1['Graph_Type']
    # vz2=eval((vz1.to_string(index=False)))

    # th=5

    # No measure column
    if (cont_columns == 0 and cat_columns == 0):
        print('No measure to plot!')

    # One continuos data column -> Histogram
    elif (cont_columns == 1 and cat_columns == 0 and dt_columns == 0):
        vz1 = vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vbar_chart_hzz2(df,usr_name)
        # histogram(df)

    # two continuous  -> Scatter plot
    elif (cont_columns == 2 and cat_columns == 0 and dt_columns == 0):
        vz1 = vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)
        # return scatter(df)

    # three continuous  -> bubble plot
    elif (cont_columns == 3 and cat_columns == 0 and dt_columns == 0):
        vz1 = vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)
        # bubble_three(df)

    # three continuous one categorical -> bubble plot
    elif (cont_columns == 3 and cat_columns == 1 and dt_columns == 0):
        vz1 = vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)
        # bubble_four(df)

    # One continuos data column and one datetime -> line chart
    elif (cont_columns >= 1 and cat_columns == 0 and dt_columns == 1):
        vz1 = vz_cond[(vz_cond['Continuous'].between(1, cont_columns)) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)
        # Line_graph(df)

    # more than three continuous  -> heat map
    elif (cont_columns >= 4 and cat_columns == 1 and dt_columns == 0):
        vz1 = vz_cond[(vz_cond['Continuous'].between(4, cont_columns)) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)
        # Heat_map(df)

    # two continuous and one categorical -> Scatter plot - with color for one category (min number of unique category)
    elif (cont_columns == 2 and cat_columns == 1 and dt_columns == 0):
        vz1 = vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns) & (
                    vz_cond['Date_Time'] == dt_columns)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)
        # scatter_plot(df)

    # One continuos data column and one categorical data column -> Column chart
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 0 and len(df_categorical.iloc[:, 0].unique()) < 5):
        vz1 = vz_cond[(vz_cond['Continuous'] == cont_columns) & (vz_cond['Categorical'] == cat_columns) &
                    (vz_cond['Date_Time'] == dt_columns) & (vz_cond['no_categories'].between(1, 4)) & (
                                vz_cond['Length_Text_col'] == 0)]
        vz1 = vz1['Graph_Type']
        vz2 = eval((vz1.to_string(index=False)))
        return vz2(df,usr_name)


    # th=5
    # One continuos data column and one categorical data column 
    # Logic to get the Bar Plots
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 0):  # th
        print("len(df_categorical.iloc[:, 0].unique()) is: \n", len(df_categorical.iloc[:, 0].unique()))
        if df[df_categorical.columns[0]].str.len().max() < 10:
            print("About to plot BAR Vertical Graph")
            return bar_chart_vr(df, usr_name)
        else:
            print("About to plot BAR Horizontal Graph")
            return bar_chart_hz(df, usr_name)

    # One continuos data column and one datetime, one categorical and # categories <= threshold -> Stacked column chart
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 1 and len(df_categorical.iloc[:, 0].unique()) <= 5):
        print('Stacked Column Chart')

    # One continuos data column and one datetime, one categorical and # categories > threshold -> Stacked area chart
    elif (cont_columns == 1 and cat_columns == 1 and dt_columns == 1 and len(df_categorical.iloc[:, 0].unique()) > 5):
        print('Stacked Area Chart')

    # two categorical  -> column chart (Choose the column with less categories as dimesion and other column as Count measure)
    elif (cont_columns == 0 and cat_columns == 2 and dt_columns == 0):
        print('Column Chart')


    # three continuous and two categorical -> Bubble chart
    elif (cont_columns == 3 and cat_columns == 1 and dt_columns == 0):
        print('Bubble Chart')



    # one continuous two categorical -> heat map
    elif (cont_columns >= 1 and cat_columns == 2 and dt_columns == 0):
        print('Heat Chart')

    else:
        print('Unable to find the right graph!')


# print("generating heat map")
#out_put(df,"a")
#usr_name="a"
