from flask import Flask
from flask import request
from flask import json
from flask import jsonify
from flask_cors import CORS, cross_origin
from nl_sql_ver32_R import build_response
#from Dhi_spacy_V2 import build_response
from nl_sql_ver32_R import get_metadata
from nl_sql_ver32_R import frame_json_response
from visualization import out_put

app = Flask(__name__)
CORS(app)

@app.route("/hello")
def hello():
    return "Hello World!"


@app.route('/api/query/intelligence1', methods=['POST'])
def queryIntelligence1():
    print('request json : ', request.is_json)
    content = request.get_json()
    print("JSON request is: ", json.dumps(content))
    try:
        print(str(content['clientTextMessage']))
        print('userName : ', content['userName'])
        print('dataFileName : ', content['dataFileName'])
        print('aliasFileName : ', content['aliasFileName'])
        tabular_data = build_response(str(content['clientTextMessage']),'test_user', content['dataFileName'], content['aliasFileName'])
        return tabular_data
    except Exception as error:
        print('Error in controller : ', error)
        return jsonify(
            textMessage="Sorry!, I could not understand. Could you please rephrase your question?",
            imageUrl='',
            status='success'
        )


@app.route('/api/alias/meta-data', methods=['POST'])
def alias_metadata():
    print('request json : ', request.is_json)
    content = request.get_json()
    try:
        print('Path : ', content['dataFilePath'])
        #table_name, column_info, column_type, df_metadata = get_metadata(content['dataFilePath'])
        table_name,column_info,column_type,col_type_numeric,col_type_object, df_metadata = get_metadata(content['dataFilePath'])
        print(df_metadata)
        #table_name, column_info, column_type, df_metadata = get_metadata('C:/dhi_intelligence/AI/nl_sql_sample_data_2.csv')
        meta_data = frame_json_response(df_metadata)
        return jsonify(
            discovery=meta_data,
            status='success'
        )
    except Exception as error:
        print('Error in controller : ', error)
        return jsonify(
            textMessage="Error occured from the AI model",
            imageUrl='',
            status='success'
        )


# @app.route('/api/query/intelligence', methods=['POST'])
# def queryIntelligence():
#     print('request json : ', request.is_json)
#     content = request.get_json()
#     print('clientTextMessage : ', content['clientTextMessage'])
#     #print('content : ', content)
#     #text = "This is the sample text from python server"
#     #print('Text message from the function : ', text)
#     try:
#         responseValue = build_response(str(content['clientTextMessage']))
#         print('Response Value :', responseValue)
#         #responseValue1 = responseValue.to_json()
#         #print('Response value : ', responseValue1)
#         usr_name="test_user"
#         print('invoking response value ')
#         print('Before invoking output function df : ', responseValue)
#         image_path = out_put(responseValue, usr_name)
#         print('Image path : ', image_path)
#         responseValue = responseValue.to_json()
#         print('Response value : ', responseValue)
#     except Exception as error:
#         return jsonify(
#             textMessage="Sorry!, I could not understand. Could you please rephrase your question?",
#             imageUrl='',
#             status='success'
#         )
#     # out_put(df)
#     #return responseValue;
#     return jsonify(
#     textMessage=responseValue,
#     imageUrl=image_path,
#     status='success'
#     )

if __name__ == "__main__":
    app.run()


