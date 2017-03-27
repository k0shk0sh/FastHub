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

function scrollTo(hash) {
    var element = document.getElementById("user-content-" + hash);
    element.scrollIntoView();
}

window.onclick = function(e) {
    if (e.target.localName == 'a') {
        var href = e.target;
        href = href.toString().replace("file:///android_asset/md/","");
        console.log(href);
        if (href.indexOf("#") === 0) {
            scrollTo(href.replace("#",""));
        }
    }
};