/*!
 * reveal-code-focus 1.0.0
 * Copyright 2015-2017 Benjamin Tan <https://demoneaux.github.io/>
 * Available under MIT license <https://github.com/demoneaux/reveal-code-focus/blob/master/LICENSE>
 *
 * Modified by @gitpitch with support for updateCodeOpacity() and
 * resetCodeOpacity() respectively.
 */
;(function(window, Reveal, hljs) {
  if (typeof window.RevealCodeFocus == 'function') {
    return;
  }

  var currentSlide, currentFragments, scrollToFocused = true, prevSlideData = null;

  // Iterates through `array`, running `callback` for each `array` element.
  function forEach(array, callback) {
    var i = -1, length = array ? array.length : 0;
    while (++i < length) {
      callback(array[i]);
    }
  }

  function indexOf(array, elem) {
    var i = -1, length = array ? array.length : 0;
    while (++i < length) {
      if (array[i] === elem) {
        return i;
      }
    }
  }

  function initialize(e) {
    // Initialize code only once.
    // TODO: figure out why `initialize` is being called twice.
    if (initialize.ran) {
      return;
    }
    initialize.ran = true;

    // TODO: mark as parsed.
    forEach(document.querySelectorAll('pre code'), function(element) {
      // Trim whitespace if the `data-trim` attribute is present.
      if (element.hasAttribute('data-trim') && typeof element.innerHTML.trim == 'function') {
        element.innerHTML = element.innerHTML.trim();
      }

      // Highlight code using highlight.js.
      hljs.highlightBlock(element);

      // Split highlighted code into lines.
      var openTags = [], reHtmlTag = /<(\/?)span(?:\s+(?:class=(['"])hljs-.*?\2)?\s*|\s*)>/g;
      element.innerHTML = element.innerHTML.replace(/(.*?)\r?\n/g, function(_, string) {
        if (!string) {
          return '<span class=line>&nbsp;</span>';
        }
        var openTag, stringPrepend;
        // Re-open all tags that were previously closed.
        if (openTags.length) {
          stringPrepend = openTags.join('');
        }
        // Match all HTML `<span>` tags.
        reHtmlTag.lastIndex = 0;
        while (openTag = reHtmlTag.exec(string)) {
          // If it is a closing tag, remove the opening tag from the list.
          if (openTag[1]) {
            openTags.pop();
          }
          // Otherwise if it is an opening tag, push it to the list.
          else {
            openTags.push(openTag[0]);
          }
        }
        // Close all opened tags, so that strings can be wrapped with `span.line`.
        if (openTags.length) {
          string += Array(openTags.length + 1).join('</span>');
        }
        if (stringPrepend) {
          string = stringPrepend + string;
        }
        return '<span class=line>' + string + '</span>';
      });
    });

    Reveal.addEventListener('slidechanged', updateCurrentSlide);

    Reveal.addEventListener('fragmentshown', function(e) {
      focusFragment(e.fragment);
    });

    // TODO: make this configurable.
    // When a fragment is hidden, clear the current focused fragment,
    // and focus on the previous fragment.
    Reveal.addEventListener('fragmenthidden', function(e) {
      var index = indexOf(currentFragments, e.fragment);
      focusFragment(currentFragments[index - 1]);
    });

    updateCurrentSlide(e);
  }
  initialize.ran = false;

  function updateCurrentSlide(e) {
    currentSlide = e.currentSlide;
    currentFragments = currentSlide.getElementsByClassName('fragment');
    clearPreviousFocus();

    // If moving back to a previous slide…
    if (
        currentFragments.length &&
        prevSlideData &&
        (
          prevSlideData.indexh > e.indexh ||
          (prevSlideData.indexh == e.indexh && prevSlideData.indexv > e.indexv)
        )
    ) {
      // …return to the last fragment and highlight the code.
      while (Reveal.nextFragment()) {}
      var currentFragment = currentFragments[currentFragments.length - 1];
      currentFragment.classList.add('current-fragment');
      focusFragment(currentFragment);
    }

    // Update previous slide information.
    prevSlideData = {
      'indexh': e.indexh,
      'indexv': e.indexv
    };

  }

  // Removes any previously focused lines.
  function clearPreviousFocus() {
    forEach(currentSlide.querySelectorAll('pre code .line.focus'), function(line) {
      line.classList.remove('focus');
    });
  }

  function updateCodeOpacity() {

    linesOfCode = currentSlide.querySelectorAll('pre code .line')
    linesInFocus = currentSlide.querySelectorAll('pre code .line.focus')

    if(linesInFocus.length == 0) {
        // No linesInFocus, make all lines full opacity.
        forEach(linesOfCode, function(line) {
          line.style.opacity = 1.0;
        });
    } else {
        // Some linesInFocus, reduce opacity on all linesNotInFocus.
        forEach(linesOfCode, function(line) {
            if(line.classList.contains('focus')) {
                line.style.opacity = 1.0;
            } else {
                line.style.opacity = 0.20;
            }
        });
    }

  }

  function resetCodeOpacity() {

    linesOfCode = currentSlide.querySelectorAll('pre code .line')

    forEach(linesOfCode, function(line) {
      line.style.opacity = 1.0;
    });

  }

  function focusFragment(fragment) {

    if(fragment != undefined) {
      linesOfCode = currentSlide.querySelectorAll('pre code .line');
      if(linesOfCode != undefined && linesOfCode.length != 0) {
        var fidx = indexOf(currentFragments, fragment) + 1;
        pushCodePresentingStepNotification(fidx, currentFragments.length);
      }
    }

    clearPreviousFocus();
    if (!fragment) {
      resetCodeOpacity();
      return;
    }

    var lines = fragment.getAttribute('data-code-focus');
    if (!lines) {
      return;
    }

    var codeBlock = parseInt(fragment.getAttribute('data-code-block'));
    if (isNaN(codeBlock)) {
      codeBlock = 1;
    }

    var preElems = currentSlide.querySelectorAll('pre');
    if (!preElems.length) {
      return;
    }

    var pre = preElems[codeBlock - 1];
    var code = pre.querySelectorAll('code .line');
    if (!code.length) {
      return;
    }

    var topLineNumber, bottomLineNumber;

    forEach(lines.split(','), function(line) {
      var parsed = parseDataCodeFocus(line);
      forEach(parsed, function(line) {
        focusLine(line);
      });
    });

    updateCodeOpacity();

    function parseDataCodeFocus(dataCodeFocus) {
      //
      // Parse data-code-focus attribute data:
      //
      // Examples:
      // [1], [1, 2], [1-2], [1-3, 5], [6, 9-11]
      //
      var parsed = new Array();
      try {
        var dcfParts = dataCodeFocus.split(",");
        forEach(dcfParts, function(dcfPart) {
          var frag = dcfPart.split('-');
          if(frag.length == 1) {
            parsed.push(frag);
          } else {
            var i = frag[0] - 1, j = frag[1];
            while (++i <= j) {
              parsed.push(i);
            }
          }
        });
      } catch(ex) {
        // Ignore bad data-code-focus syntax
      }

      return parsed;
    }

    function focusLine(lineNumber) {
      // Convert from 1-based index to 0-based index.
      lineNumber -= 1;

      var line = code[lineNumber];
      if (!line) {
        return;
      }

      line.classList.add('focus');

      if (scrollToFocused) {
        if (topLineNumber == null) {
          topLineNumber = bottomLineNumber = lineNumber;
        } else {
          if (lineNumber < topLineNumber) {
            topLineNumber = lineNumber;
          }
          if (lineNumber > bottomLineNumber) {
            bottomLineNumber = lineNumber;
          }
        }
      }
    }

    if (scrollToFocused && topLineNumber != null) {
      var topLine =  code[topLineNumber];
      var bottomLine = code[bottomLineNumber];
      var codeParent = topLine.parentNode;
      var scrollTop = topLine.offsetTop;
      var scrollBottom = bottomLine.offsetTop + bottomLine.clientHeight;
      codeParent.scrollTop = scrollTop - (codeParent.clientHeight - (scrollBottom - scrollTop)) / 2;
    }

  }

  function RevealCodeFocus(options) {
    options || (options = {
      'scrollToFocused': true
    });

    if (options.scrollToFocused != null) {
      scrollToFocused = options.scrollToFocused;
    }

    if (Reveal.isReady()) {
      initialize({ 'currentSlide': Reveal.getCurrentSlide() });
    } else {
      Reveal.addEventListener('ready', initialize);
    }
  }

  window.RevealCodeFocus = RevealCodeFocus;
}(this, this.Reveal, this.hljs));
