function scrollToLineNumber(lineNo, secondLineNo) {
    console.log(lineNo + "\n" + secondLineNo);
    var scrollTo = secondLineNo > lineNo ? secondLineNo : lineNo;
    var div = document.querySelector('div[data-line-number="' + scrollTo + '"]');
    var scrollToPosition = scrollTo > 5 ? (scrollTo - 5) : scrollTo;
    var toScrollTo = document.querySelector('div[data-line-number="' + scrollToPosition + '"]');
    if (secondLineNo > lineNo) {
        for (var i = lineNo; i < secondLineNo + 1; i++) {
            var highlighted = document.querySelector('div[data-line-number="' + i + '"]');
            if(highlighted != null)
                highlighted.parentElement.style.backgroundColor = "rgb(3, 102, 214)";

            if(secondLineNo == i) break;
        }
    } else {
        div.parentElement.style.backgroundColor = "rgb(248, 238, 199)";
    }
    if (toScrollTo != null) smoothScroll(toScrollTo);
}

function currentYPosition() {
    if (document.body.scrollTop) return document.body.scrollTop;
    return 0;
}

function elmYPosition(element) {
    var elm = element;
    var y = elm.offsetTop;
    var node = elm;
    while (node.offsetParent && node.offsetParent != document.body) {
        node = node.offsetParent;
        y += node.offsetTop;
    }
    return y;
}

function smoothScroll(element) {
    var startY = currentYPosition();
    var stopY = elmYPosition(element);
    var distance = stopY > startY ? stopY - startY : startY - stopY;
    if (distance < 100) {
        scrollTo(0, stopY);
        return;
    }
    var speed = Math.round(distance / 100);
    if (speed >= 20) speed = 20;
    var step = Math.round(distance / 25);
    var leapY = stopY > startY ? startY + step : startY - step;
    var timer = 0;
    if (stopY > startY) {
        for (var i = startY; i < stopY; i += step) {
            setTimeout("window.scrollTo(0, " + leapY + ")", timer * speed);
            leapY += step;
            if (leapY > stopY) leapY = stopY;
            timer++;
        }
        return;
    }
    for (var i = startY; i > stopY; i -= step) {
        setTimeout("window.scrollTo(0, " + leapY + ")", timer * speed);
        leapY -= step;
        if (leapY < stopY) leapY = stopY;
        timer++;
    }
}