from flask import Flask
app = Flask(__name__)

@app.route('/register')
def register():
    if request.method == 'POST':
        return "PlaceHolder"

@app.route('/install')
def install():
    if request.method == 'POST':
        return "PlaceHolder"

@app.route('/uninstall')
def uninstall():
    if request.method == 'POST':
        return "PlaceHolder"

@app.route('/update')
def update():
    if request.method == 'POST':
        return "PlaceHolder"

@app.route('/recommend')
def recommend():
    if request.method == 'POST':
        return "PlaceHolder"

if __name__ == '__main__':
    app.run(port='80')
