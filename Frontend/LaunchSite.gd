extends Spatial

# Declare member variables here. Examples:
var position = Vector3()

var pxArray = []
var px = []
var pyArray = []	# Used for temporary storage until the next array replace
var py = []			# The array used by the simulation
var pzArray = []
var pz = []

var rx = []
var rxArray = []
var ry = 0.0
var rz = 0.0

var crx = 0.0

var currentCam = 0
var currentFrame = 0
var setupAndReady = false	# Used to wait 10 frames for the simulation to get going
# var b = "text"

# Called when the node enters the scene tree for the first time.
func _ready():
	var query = "reset=y"	# The required post data to be transmitted when the frontend starts
	# Sets up an HTTP request with the address, headers, SSL or not, Method, and the Post parameters
	$HTTPRequest.request("http://0.0.0.0:3000", ["Content-Type: application/x-www-form-urlencoded"], false, HTTPClient.METHOD_POST, query)
	$EarthAlpha/FollowCam.make_current()

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	if setupAndReady:						# When setup, begin the simulation
		position.x = px[currentFrame]		# Loops through the used array
		position.y = py[currentFrame]		# Loops through the used array
		position.z = pz[currentFrame]		# Loops through the used array
		crx = $EarthAlpha.global_transform.basis.get_euler().x
		if position.y < 0.0:
			print(position.y)
			print("The Rocket Has Landed")
		else:
			$EarthAlpha.global_rotate(Vector3(1,0,0), (rx[currentFrame] - crx))
#			$EarthAlpha.global_rotate(Vector3(1,0,0), PI)
			$EarthAlpha.global_translate(position) # Move the rocket according to its location
	currentFrame += 1									# Increment the current frames
	if currentFrame	== 10:
		$HTTPRequest.request("http://0.0.0.0:3000")		# Starts a network communication once every n frames, too many and it lags
		setupAndReady = true							# A flag for the simulation to start
		px = pxArray									# Every 10 frames, use a new array of positions
		py = pyArray
		pz = pzArray
		rx = rxArray
		currentFrame = 0								# Set the currentFrame back to 0 for array indexing
		

# The function that deals with the returns from the backend in an async sort of way
func _on_HTTPRequest_request_completed(result, response_code, headers, body):
	var json = JSON.parse(body.get_string_from_utf8())
	pxArray = json.result["px"]
	pyArray = json.result["py"]
	pzArray = json.result["pz"]
	rxArray = json.result["rx"]
	# position = Vector3(json.result["px"], json.result["py"], json.result["pz"])

# Depending on which one of the arrow keys is pressed, switch to that camera
func _input(ev):
	if Input.is_key_pressed(KEY_LEFT):
		$Ground/GroundCam.make_current()
	elif Input.is_key_pressed(KEY_DOWN):
		$EarthAlpha/FollowCam.make_current()
	elif Input.is_key_pressed(KEY_UP):
		$EarthAlpha/BaseCam.make_current()
	 