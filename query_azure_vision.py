import requests
import matplotlib.pyplot as plt
from io import BytesIO
from multiprocessing import Process, Manager
def connect_vision_api(subscription_key,image_data):
	#assert subscription_key
	#print('sub key',subscription_key)
	#print('image data',len(image_data))
	vision_base_url = 'https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/'
	analyze_url = vision_base_url + "analyze"
	headers = {'Ocp-Apim-Subscription-Key': subscription_key,
				'Content-Type': 'application/octet-stream'}
	params     = {'visualFeatures': 'Categories,Description,Color'}			
	response = requests.post(analyze_url,headers=headers,params=params,data=image_data)
	response.raise_for_status()
	# The 'analysis' object contains various fields that describe the image. The most
	# relevant caption for the image is obtained from the 'description' property.
	analysis = response.json()
	return analysis

if __name__ == '__main__':
	subscription_key = "01c8333210854059a6c2df093bf1b284"
	assert subscription_key
	image_path = "D:/name.jpg"
	image_data = open(image_path, "rb").read()
	manager = Manager()
	analysis = manager.list()
	print('here already',len(image_data))
	p = Process(target=connect_vision_api,args=(subscription_key,image_data))
	p.start()
	p.join()
	print('analysis',analysis)
	try:
	    image_caption = analysis["description"]["captions"][0]["text"].capitalize()
	except:
	    print('no caption found')
	    image_caption = 'No image caption'
# Display the image and overlay it with the caption.
