from flask import Flask, request, Response
from PIL import Image, ImageTk
import tkinter as tk
import threading
import io
from io import BytesIO

# Create an instance of the Flask class
app = Flask(__name__)

# Create the Tkinter GUI
root = tk.Tk()

# Define the route
@app.route("/send-image", methods=["POST"])
def send_image():
    # Get the image data from the request body
    image_data = request.data

    print(image_data)

    image2 = Image.open(BytesIO(image_data))
    image2.show()

    return Response(status=200)

def start_flask_app():
    # Start the Flask app
    app.run(host='0.0.0.0')

if __name__ == '__main__':
    # Start the Flask app in a separate thread
    threading.Thread(target=start_flask_app).start()

    # Start the main loop of the Tkinter GUI
    root.mainloop()