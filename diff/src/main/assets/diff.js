function loadDiff(diff) {
  var diffHtml = Diff2Html.getPrettyHtml(diff, {
    inputFormat: 'diff',
    showFiles: true,
    matching: 'none',
    outputFormat: 'line-by-line'
  });
  console.log(diffHtml);
  document.getElementById("diff").innerHTML = diffHtml;
}