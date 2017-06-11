function scrollToLineNumber(lineNo) {
    var div = document.querySelector('div[data-line-number="' + lineNo + '"]');
    lineNo = lineNo - 5;
    var toScrollTo = document.querySelector('div[data-line-number="' + lineNo + '"]');
    div.parentElement.style.backgroundColor = "rgb(248, 238, 199)";
    if(toScrollTo != null) smoothScroll(toScrollTo);
}
var smoothScroll = function (elementId) {
    var MIN_PIXELS_PER_STEP = 16;
    var MAX_SCROLL_STEPS = 30;
    var target = elementId;
    var scrollContainer = target;
    do {
        scrollContainer = scrollContainer.parentNode;
        if (!scrollContainer) return;
        scrollContainer.scrollTop += 1;
    } while (scrollContainer.scrollTop == 0);

    var targetY = 0;
    do {
        if (target == scrollContainer) break;
        targetY += target.offsetTop;
    } while (target = target.offsetParent);

    var pixelsPerStep = Math.max(MIN_PIXELS_PER_STEP,
        (targetY - scrollContainer.scrollTop) / MAX_SCROLL_STEPS);

    var stepFunc = function () {
        scrollContainer.scrollTop = Math.min(targetY, pixelsPerStep + scrollContainer.scrollTop);
        if (scrollContainer.scrollTop >= targetY) {
            return;
        }
        window.requestAnimationFrame(stepFunc);
    };
    window.requestAnimationFrame(stepFunc);
};