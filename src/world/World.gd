extends Node2D

var world_speed = 120
var grow_speed_adjust = 0.1 # see get_grow_speed()

const SCREEN_CHECK = 50
var screen_checker = SCREEN_CHECK

const NAME_FADE_SPEED = 0.5

var ready = false
var PC = preload("res://src/world/PlayerCircle.tscn")
var PlayerCircle
const PC_WAIT = 1
var pc_accumulator = 0
var pc_linger = false # disables pc_accumulator right after despawn

var level_id # World -> Level -> Circles -> Circle
var hideLevelName = false
var won = false
var level_time = 0
var level_times = Array()
const MAX_LEVEL = 20

# Level Loading

func _ready():
	for i in MAX_LEVEL:
		level_times.append(0)
	$DebugGUI/FPS.visible = false
	enable_continue(false)
	level_id = 1
	$AudioStreamPlayer.playing = true
	move_to_level(level_id)

func reload_level():
	if level_id == null:
		level_id = 0
	move_to_level(level_id)

func switch_level(forward):
	if level_id == null:
		level_id = 0
	var change = 1
	if !forward:
		change = -1
	move_to_level(level_id+change)

func move_to_level(id):
	$GUI/ScoreBox.set_visibility(false, level_times)
	ready = false
	var base = "res://src/world/levels/Level_" # 001.tscn
	var idstr = String(id)
	match idstr.length():
		1:
			idstr = "00"+idstr
		2:
			idstr = "0"+idstr
	var path = base+idstr+".tscn"
	if ResourceLoader.exists(path):
		unload_level()
		level_id = id
		call_deferred("load_level", path)
	else:
		ready = true

func load_level(path):
	var Level = load(path).instance()
	if Level != null:
		add_child(Level)
		reset_LevelName()
		won = false
		level_time = 0
		screen_checker = SCREEN_CHECK
		$GUI/Stopwatch.visible = $Level.Stopwatch_enabled
	ready = true

func unload_level():
	for emitter in get_tree().get_nodes_in_group("one_shot_emitters"):
		emitter.queue_free()
	pc_accumulator = 0
	pc_linger = false
	release_pc()
	if get_node_or_null("Level") != null:
		$Level.queue_free()
		remove_child($Level)
	$GUI/LevelName.hide()
	$GUI/LevelName/Timer.stop()
	hideLevelName = false
	enable_continue(false)

func reset_LevelName():
	if get_node_or_null("Level") != null:
		$GUI/LevelName.text = $Level.level_name
	$GUI/LevelName.modulate.a = 0
	$GUI/LevelName.show()
	$GUI/LevelName/Timer.start()

func _on_LevelNameTimer_timeout():
	hideLevelName = true
	
func enable_continue(enable): # on Level-End
	$GUI/Continue.disabled = !enable
	if (enable):
		$GUI/Continue.show()
	else:
		$GUI/Continue.hide()

func _on_Continue_button_up():
	enable_continue(false)
	switch_level(true)
	$GUI/Stopwatch.visible = false

# World Steps

func _input(event):
	if ready:
		# Player Circle
		if get_node_or_null("Level") != null:
			if InputMap.event_is_action(event, "ui_select"):
				if !has_node("PlayerCircle"):
					if !event.is_pressed():
						if pc_accumulator >= PC_WAIT:
							spawn_pc(get_viewport().get_mouse_position())
						pc_accumulator = 0
					pc_linger = false
				elif Geometry.is_point_in_circle(get_viewport().get_mouse_position(), $PlayerCircle.position, Main.PC_RADIUS):
					new_particle_oneshot($PlayerCircle.position, 0, Circle.ColorType.WHITE, "player_smoke", 1, 1)
					release_pc()
					pc_accumulator = 0
					pc_linger = true
		# Controls
		if event.is_pressed():
			if InputMap.event_is_action(event, "ui_left"):
				switch_level(false)
			elif InputMap.event_is_action(event, "ui_right"):
				switch_level(true)
			elif InputMap.event_is_action(event, "ui_reload"):
				reload_level()
			elif InputMap.event_is_action(event, "ui_fps"):
				$DebugGUI/FPS.visible = !$DebugGUI/FPS.visible
			elif InputMap.event_is_action(event, "ui_times"):
				$GUI/ScoreBox.switch_visibility(level_times)
			elif InputMap.event_is_action(event, "ui_timer"):
				$GUI/Stopwatch.visible = !$GUI/Stopwatch.visible
			elif InputMap.event_is_action(event, "ui_accept"):
				if won:
					_on_Continue_button_up()
			elif InputMap.event_is_action(event, "toggle_fullscreen"):
				OS.window_fullscreen = !OS.window_fullscreen
			elif InputMap.event_is_action(event, "toggle_music"):
				$AudioStreamPlayer.stream_paused = !$AudioStreamPlayer.stream_paused

func _process(delta):
	# LevelName
	if hideLevelName:
		$GUI/LevelName.modulate.a -= NAME_FADE_SPEED * delta
		if $GUI/LevelName.modulate.a <= 0.0:
			$GUI/LevelName.hide()
			hideLevelName = false
	elif $GUI/LevelName.modulate.a != 1.0:
		$GUI/LevelName.modulate.a += NAME_FADE_SPEED * delta
		if $GUI/LevelName.modulate.a >= 1.0:
			$GUI/LevelName.modulate.a = 1.0
	if get_node_or_null("Level") != null:
		# LevelTime
		if !won:
			level_time += delta
		if $GUI/Stopwatch.visible:
			$GUI/Stopwatch.text = String(floor(level_time))
		# Check Level-End
		if !won && victory_check():
			won = true
			if level_id > 0 && level_id-1 < level_times.size():
				level_times[level_id-1] = level_time
			if level_id == MAX_LEVEL:
				$GUI/ScoreBox.set_visibility(true, level_times)
			else:
				enable_continue(true)

func _physics_process(delta): 
	free_emitters()
	if get_node_or_null("Level") != null:
		if !won && Input.is_action_pressed("ui_select"):
			# Focus Power
			var hit
			var mouse = get_viewport().get_mouse_position()
			var focusing = false
			if $Level.FocusPower_enabled && pc_accumulator == 0:
				var r
				for c in $Level/Circles.get_children():
					r = c.radius
					if r < Circle.CIRCLE_BUTTON_MIN_RADIUS:
						r = Circle.CIRCLE_BUTTON_MIN_RADIUS
					if !c.merging_away && Geometry.is_point_in_circle(mouse, c.position, r):
						hit = c
						focus_power(c, delta)
						focusing = true
						break
			# Player Circle - input
			var button_hit = false
			if !focusing && $Level.has_node("Statics"): # "No hypo-circle over buttons"
				for s in $Level/Statics.get_children():
					if s.is_in_group("button"):
						var polygon:PoolVector2Array = [Vector2(s.position.x-50, s.position.y-50), Vector2(s.position.x-50, s.position.y+50), Vector2(s.position.x+50, s.position.y+50), Vector2(s.position.x+50, s.position.y-50)]
						if Geometry.is_point_in_polygon(mouse, polygon):
							button_hit = true
							pc_accumulator = 0
							break
			if $Level.PlayerCircle_enabled && hit == null && !has_node("PlayerCircle") && !pc_linger && !button_hit:
				pc_accumulator += delta
			# Hypothetical Circle
			if pc_accumulator > 0:
				$HypoCircle.position = mouse
		# Player Circle - update
		if has_node("PlayerCircle"):
			if $PlayerCircle/Area2D.get_overlapping_bodies().size() > 1:
				var i = 0
				for b in $PlayerCircle/Area2D.get_overlapping_bodies():
					if !b.is_in_group("circles") && !b.is_in_group("pc"):
						i += 1
				if i > 0:
					release_pc()
		# Screen Edge
		screen_checker += 1
		if screen_checker >= SCREEN_CHECK:
			for c in $Level/Circles.get_children(): # Move check to the Focus Power loop above (for performance)?
				c.check_edge_portal()
			screen_checker = 0
		update()

func victory_check():
	var w: int = 0
	var b: int = 0
	var g: int = 0
	var r: int = 0
	for c in $Level/Circles.get_children():
		match c.color_type:
			Circle.ColorType.WHITE:
				w += 1
			Circle.ColorType.BLUE:
				b += 1
			Circle.ColorType.GREEN:
				g += 1
			Circle.ColorType.RED:
				r += 1
		if c.stuck_timer > 0: # no victory unless no circles are stuck
			w += 2
	return w <= 1 && b <= 1 && g <= 1 && r <= 1

func _draw():
	# Player Circle
	if get_rays_enabled():
		# "Partial Circle"
		if pc_accumulator > 0:
			var angle = deg2rad(360*(pc_accumulator / PC_WAIT))
			var color = Color.white
			if !could_spawn_pc_now():
				color = Color(0.7, 0, 0)
			draw_arc(get_viewport().get_mouse_position(), Main.PC_RADIUS, 0, angle, 40, color, 2, true)
	# Rays
	if get_node_or_null("Level") != null:
		var draw_rays = get_rays_enabled() && could_spawn_pc_now()
		for c in $Level/Circles.get_children():
			if draw_rays && c.ray_point_a != null && c.ray_point_b != null:
				draw_line(c.position+c.position.direction_to(c.ray_point_a)*c.size*PI, c.ray_point_a, Circle.ct_dict.get(c.color_type).get("color"), 2, true)
				draw_line(c.ray_point_a, c.ray_point_b, Circle.ct_dict.get(c.color_type).get("color"), 2, true)
				c.get_node("RayArrow").position = c.to_local(c.ray_point_b)
				c.get_node("RayArrow").rotation = c.ray_point_a.angle_to_point(c.ray_point_b)
				c.get_node("RayArrow").visible = true
				#draw_circle(c.position+c.get_node("RayCastA").position, 5, Color.red)
				#draw_circle(c.position+c.get_node("RayCastB").position, 5, Color.red)
			else:
				c.get_node("RayArrow").visible = false

# Player Circle

func get_rays_enabled():
	if get_node_or_null("Level") != null:
		return $Level.PlayerCircle_enabled && Input.is_action_pressed("ui_select") && !has_node("PlayerCircle") && !won && pc_accumulator > 0
	else:
		return false

func could_spawn_pc_now():
	return !won && $HypoCircle.get_overlapping_bodies().size() == 0

func spawn_pc(pos):
	if get_node_or_null("Level") != null && $Level.PlayerCircle_enabled && could_spawn_pc_now():
		PlayerCircle = PC.instance()
		PlayerCircle.position = pos
		add_child(PlayerCircle)
		new_particle_oneshot(pos, 0, Circle.ColorType.WHITE, "shine", 1, 1)

func release_pc():
	if get_node_or_null("PlayerCircle") != null:
		PlayerCircle.queue_free()

# Resizing Circles ("Focus Power")

func focus_power(circle, delta):
	if get_node_or_null("Level") != null:
		var valid = Array()
		var color_type = circle.color_type
		var min_size = circle.color_info.get("lowest_power")
		for c in $Level/Circles.get_children():
			if c != circle && c.color_type == color_type && !c.merging_away && c.size > min_size && c.size+c.grow_buffer > min_size:
				valid.append(c)
		if valid.size() > 0:
			var max_grow = get_grow_speed()*delta
			var spare_fade = max_grow
			var fade = max_grow / valid.size()
			for c in valid:
				if c.size-fade > min_size && c.size+c.grow_buffer > min_size:
					spare_fade -= fade
					c.set_size(c.size-fade)
				else:
					spare_fade -= c.size-min_size
					c.set_size(min_size)
			circle.set_size(circle.size+max_grow-spare_fade)

func get_grow_speed():
	return world_speed*grow_speed_adjust

# Particle Effects

var BounceParticles = preload("res://src/world/circle_effects/Bounce.tscn")
var SplitByBeamParticles = preload("res://src/world/circle_effects/SplitByBeam.tscn")
var PlayerSmokeParticles = preload("res://src/world/circle_effects/PlayerSmoke.tscn")
var ShineParticles = preload("res://src/world/circle_effects/CircleShine.tscn")

func free_emitters():
	for emitter in get_tree().get_nodes_in_group("one_shot_emitters"):
		if not emitter.emitting:
			emitter.queue_free()

func new_particle_oneshot(pos, rot, color_type, effect_name, multiply_amount, s):
	var color = Circle.ct_dict.get(color_type).get("color")
	var particles
	match effect_name:
		"bounce":
			particles = BounceParticles.instance()
			particles.modulate = color
		"split_by_beam":
			particles = SplitByBeamParticles.instance()
		"player_smoke":
			particles = PlayerSmokeParticles.instance()
		"shine":
			particles = ShineParticles.instance()
	if particles != null:
		particles.scale = Vector2(s, s)
		particles.amount *= multiply_amount
		particles.position = pos
		particles.rotation = rot
		particles.emitting = true
		add_child(particles)
