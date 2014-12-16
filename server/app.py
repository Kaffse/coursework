from flask import Flask
import json
app = Flask(__name__)

@app.route('/register')
def register():
    if request.method == 'POST':
        print request.get_json()
        data = {'success':'true'}
        return json.dumps(data)

@app.route('/install')
def install():
    if request.method == 'POST':
        print request.get_json()
        data = {'list':[]}
        return json.dumps(data)

@app.route('/uninstall')
def uninstall():
    if request.method == 'POST':
        print request.get_json()
        data = {'list':[]}
        return json.dumps(data)

@app.route('/update', methods = ['GET', 'POST'])
def update():
    if request.method == 'POST':
        print request.get_json()
        data = {'list':[]}
        return json.dumps(data)

@app.route('/recommend')
def recommend():
    if request.method == 'POST':
        print request.get_json()
        data = {'list':[]}
        return json.dumps(data)

if __name__ == '__main__':
    app.run(port='80')
