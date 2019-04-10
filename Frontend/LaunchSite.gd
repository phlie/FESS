extends Spatial

# Declare member variables here. Examples:
var velocity = 1
var currentCam = 0
# var b = "text"

# Called when the node enters the scene tree for the first time.
func _ready():
	$HTTPRequest.request("http://0.0.0.0:3000")
	$EarthAlpha/FollowCam.make_current()

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	$HTTPRequest.request("http://0.0.0.0:3000")
	var position = Vector3()
	position.y += delta*velocity
	$EarthAlpha.global_translate(position)



func _on_HTTPRequest_request_completed(result, response_code, headers, body):
    var json = JSON.parse(body.get_string_from_utf8())
    velocity = json.result["velocity"]

func _input(ev):
	if Input.is_key_pressed(KEY_LEFT):
		$Ground/GroundCam.make_current()
	elif Input.is_key_pressed(KEY_DOWN):
		$EarthAlpha/FollowCam.make_current()
	elif Input.is_key_pressed(KEY_UP):
		$EarthAlpha/BaseCam.make_current()
	 