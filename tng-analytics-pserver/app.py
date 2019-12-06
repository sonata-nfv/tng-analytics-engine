#!flask/bin/python
from flask import Flask, jsonify
from flask import abort
from flask import make_response
from flask import request
from flask import url_for
from flask import render_template
from datetime import datetime
from importlib import import_module
import subprocess 
import imp
from flask import render_template

app = Flask(__name__)



@app.route('/panalytics/api/v1.0/<string:analytic_module>/<string:analytic_class>/<string:analytic_function>', methods=['POST'])
def request_analytic_service(analytic_module,analytic_class,analytic_function):
    if not request.json or not 'periods' in request.json:
        abort(400)
    prometheus_url=request.json['prometheus_url']
    periods=request.json['periods']
    metrics=request.json['metrics']
    step=request.json['step']

    try:
        print(analytic_module+'.'+analytic_class)
        module = import_module(analytic_module+'.'+analytic_class)
        function_to_call = getattr(module, analytic_function)
        #to return both html and additional plots -- TODO
        response =  function_to_call(prometheus_url,periods,metrics,step);
        print(response)
        with open('templates/'+analytic_class+'.html', "w") as test:
             test.write(response)
             test.close()
    except ImportError as e:
        print (e)
        return jsonify({'message': 'Library '+analytic_module+' is not installed'}), 400
    
    return analytic_class+'.html\n', 201
    

#install new library
@app.route("/install_library/<string:repo_name>/<string:library_name>")
def install_library(repo_name,library_name):
    command_success_install_new_library = "pip3 install git+https://github.com/"+repo_name+"/"+library_name+".git"
    print(command_success_install_new_library)
    result_success = subprocess.check_output([command_success_install_new_library], shell=True)
    #register the analytic service at the tng-analytics-engine
    return jsonify({'install_library': repo_name+"/"+library_name}), 201

@app.route('/<string:file_name>')
def admin_dashboard(file_name):
    return render_template(file_name)


@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

#if __name__ == '__main__':
#    app.run(debug=True)
    
if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')
