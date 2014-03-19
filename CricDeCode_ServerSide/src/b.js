Number.random = function(min, max){
	return Math.floor(Math.random() * (max - min + 1) + min);
};

var zeros = {x:0, y:0, z:0};

jQuery.extend(jQuery.fn, {

	scatter: function(){
		return this.translate({
			x: Number.random(-1000, 1000),
			y: Number.random(-1000, 1000),
			z: Number.random(-500, 500)
		}).rotate({
			x: Number.random(-720, 720),
			y: Number.random(-720, 720),
			z: Number.random(-720, 720)
		});
	},

	unscatter: function(){ 
		return this.translate(zeros).rotate(zeros);
	},

	frighten: function(d){
		var self = this;
		this.setTransition('timing-function', 'ease-out').scatter();
		setTimeout(function(){
			self.setTransition('timing-function', 'ease-in-out').unscatter();
		}, 500);
		return this;
	},

	zoom: function(delay){
		var self = this;
		this.scale(0.01);
		setTimeout(function(){
			self.setTransition({
				property: 'transform',
				duration: '250ms',
				'timing-function': 'ease-out'
			}).scale(1.2);
			setTimeout(function(){
				self.setTransition('duration', '100ms').scale(1);
			}, 250)
		}, delay);
		return this;
	},

	makeSlider: function(){
		return this.each(function(){
			var $this = $(this),
				open = false,
				next = $this.next(),
				height = next.attr('scrollHeight'),
				transition = {
					property: 'height',
					duration: '500ms',
					transition: 'ease-out'
				};
			next.setTransition(transition);
			$this.bind('click', function(){
				next.css('height', open ? 0 : height);
				open = !open;
			});
		})
	},

	fromChaos: (function(){
		var delay = 0;
		return function(){
			return this.each(function(){
				var element = $(this);
				//element.scatter();
				setTimeout(function(){
					element.setTransition({
						property: 'transform',
						duration: '500ms',
						'timing-function': 'ease-out'
					});
					setTimeout(function(){
						element.unscatter();
						element.bind({
							mouseenter: jQuery.proxy(element.frighten, element),
							touchstart: jQuery.proxy(element.frighten, element)
						});
					}, delay += 100);
				}, 1000);
			})
		}
	}())

});


// When the DOM is ready...
$(document).ready(function() {
	
	// Get the proper CSS prefix
	var cssPrefix = false;
	if(jQuery.browser.webkit) {
		cssPrefix = "webkit";
	}
	else if(jQuery.browser.mozilla) {
		cssPrefix = "moz";
	}
	
	// If we support this browser
	if(cssPrefix) {
		// 300 x 233
		var cols = 10; // Desired columns
		var rows = 8; // Desired rows
		var totalWidth = 300; // Logo width
		var totalHeight = 233; // Logo height
		var singleWidth = Math.ceil(totalWidth / cols); // Shard width
		var singleHeight = Math.ceil(totalHeight / rows); // Shard height
		
		// Remove the text and background image from the logo
		var logo = jQuery("#homeLogo").css("backgroundImage","none").html("");
		
		// For every desired row
		for(x = 0; x < rows; x++) {
			var last;
			//For every desired column
			for(y = 0; y < cols; y++) {
				// Create a SPAN element with the proper CSS settings
				// Width, height, browser-specific CSS
				last = jQuery("<span />").attr("style","width:" + (singleWidth) + "px;height:" + (singleHeight) + "px;background-position:-" + (singleHeight * y) + "px -" + (singleWidth * x) + "px;-" + cssPrefix + "-transition-property: -" + cssPrefix + "-transform; -" + cssPrefix + "-transition-duration: 200ms; -" + cssPrefix + "-transition-timing-function: ease-out; -" + cssPrefix + "-transform: translateX(0%) translateY(0%) translateZ(0px) rotateX(0deg) rotateY(0deg) rotate(0deg);");
				// Insert into DOM
				logo.append(last);
			}
			// Create a DIV clear for row
			last.append(jQuery("<div />").addClass("clear"));
		}
		
		// Chaos!
		jQuery("#homeLogo span").fromChaos();
	}
});
