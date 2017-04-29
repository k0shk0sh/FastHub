document.addEventListener("DOMContentLoaded", function(event) {
   document.querySelectorAll('img').forEach(function(img){
  	img.onerror = function(){this.style.display='none';};
   });
});

window.onload = function() {
    addTouchEvents(document.getElementsByTagName("pre"));
    addTouchEvents(document.getElementsByTagName("table"));
    var hash = window.location.hash.substr(1);
    if (hash != ""){
        scrollTo(hash);
    }
};

function addTouchEvents(elements) {
    for (var i = 0; i < elements.length; i++) {
        elements[i].addEventListener("touchstart", touchStart, false);
        elements[i].addEventListener("touchend", touchEnd, false);
    }
}

function touchStart(event) {
    Android.startIntercept();
}

function touchEnd(event) {
    Android.stopIntercept();
}