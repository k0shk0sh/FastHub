window.onload = function () {
    var hash = window.location.hash.substr(1);
    if (hash !== "") {
        scrollTo(hash);
    }
};

function scrollTo(hash) {
    var element = document.getElementById("user-content-" + hash);
    if (element) {
        element.scrollIntoView();
    } else {
        element = document.querySelector('[href="#' + hash + '"]');
        if (element) {
            element.scrollIntoView();
        } else {
            //HACK!!!
            var names = hash.trim().split("-").join(" ");
            if (names) {
                var elements = document.querySelectorAll("h1");
                for (var index = 0; index < elements.length; index++) {
                    if (elements[index].innerText.toLowerCase() === names.toLowerCase()) {
                        elements[index].scrollIntoView();
                    }
                }
            }
        }
    }
}

window.onclick = function (e) {
    if (e.target.localName === 'a') {
        var href = e.target;
        href = href.toString().replace("file:///android_asset/md/", "");
        if (hasHashtag(href)) {
            scrollTo(href.substr(href.indexOf("#"), href.length).replace("#", ""));
        }
    }
};

function hasHashtag(url) {
    return (url.indexOf("#") !== -1);
}
