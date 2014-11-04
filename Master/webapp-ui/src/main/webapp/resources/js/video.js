var video = document.getElementById("video3");

function toggleControls() {
  if (video.hasAttribute("controls")) {
     video.removeAttribute("controls")   
  } else {
     video.setAttribute("controls","controls")   
  }
}