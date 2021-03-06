extends Switchable

export (PoolIntArray) var angles
export (bool) var reversed

var next_angle

const ROT_SPEED = 30

func _ready():
	current_state = round(rad2deg(transform.get_rotation()))
	options = angles
	._ready()

func _physics_process(delta):
	if next_angle != upcoming_state:
		next_angle = upcoming_state
	if current_state != upcoming_state:
		if round(current_state) == upcoming_state: # todo smoother stop
			rotation = deg2rad(round(current_state))
		else:
			var rot = ROT_SPEED * delta
			if (reversed && options.size() > 2) || (!reversed && options.size() == 2 && current_state > upcoming_state):
				rot *= -1
			rotate(deg2rad(rot))
			current_state = rad2deg(transform.get_rotation())
