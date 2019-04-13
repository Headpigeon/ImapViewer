var ROOT = BASE_URL + '/mail';
var API_ROOT = ROOT + '/api';

$(document).ready(function() {
    $('#refresh').click(function() {
        document.location.reload();
    });
    
    window.addEventListener("message", function(event) {
        if (typeof(event.data.newHeight) !== 'undefined') {
            // add padding for potential horizontal scrollbar
            $('#mailHtmlContent').height((event.data.newHeight + 20) + 'px');
        }
    }, false);
    
    var IFRAME_LOADED_SCRIPT = "\
<script>\
window.addEventListener('load', function(event) { \
    if (parent !== null) {\
        parent.postMessage({newHeight: document.querySelector('html').offsetHeight}, '*');\
    }\
});\
</script>\
";
    
    $('#mailListContent').on('click', '[data-message-number]', null, function(e) {
        var msgNumber = $(e.target).closest('[data-message-number]').attr('data-message-number');

        var loadText = $.get(API_ROOT + '/text/' + msgNumber);
        var loadTextSuccess = false;
        var textLoaded = $.Deferred();
        loadText.done(function(text) {
            $('#mailText').text(text);
            loadTextSuccess = true;
        }).always(function() {
            textLoaded.resolve();
        });

        var loadHtml = $.get(API_ROOT + '/html/' + msgNumber);
        var loadHtmlSuccess = false;
        var htmlLoaded = $.Deferred();
        loadHtml.done(function(html) {
            var $el = $('<div>').html(html);
            fixEmbedded($el, msgNumber);
            setIframeContent($('#mailHtmlContent')[0], $el.html() + IFRAME_LOADED_SCRIPT);
            loadHtmlSuccess = true;
        }).always(function() {
            htmlLoaded.resolve();
        });

        var loadDetails = $.get(ROOT + '/part-info/' + msgNumber);
        var detailsLoaded = $.Deferred();
        loadDetails.done(function(html) {
            $('#mailDetails').html(html);
        }).always(function() {
            detailsLoaded.resolve();
        });
        
        var loadAttachments = $.get(API_ROOT + '/attachments/' + msgNumber);
        var attachmentsLoaded = $.Deferred();
        loadAttachments.done(function(attachments) {
            var $attachmentsList = $('#attachmentList');
            $attachmentsList.find('.generated').remove();
            if (attachments.length === 0) {
                $('#attachments').addClass('d-none');
            } else {
                $('#attachments').removeClass('d-none');
                for (var i = 0; i < attachments.length; i++) {
                    $('<li>').attr('class', 'nav-item generated')
                             .append($('<a>').attr({ 'class': 'nav-link',
                                                     'href': API_ROOT + '/attachment/' + msgNumber + '/' + attachments[i] })
                                             .text(attachments[i]))
                             .appendTo($attachmentsList);
                }
            }
        }).always(function() {
            attachmentsLoaded.resolve();
        });

        $.when(textLoaded, htmlLoaded, detailsLoaded, attachmentsLoaded).always(function() {
            $('#mailTextTab').toggleClass('disabled', !loadTextSuccess);
            $('#mailHtmlTab').toggleClass('disabled', !loadHtmlSuccess);
            $('#mailDetailsTab').removeClass('disabled');

            if (loadHtmlSuccess) {
                $('#mailHtmlTab').tab('show');
            } else if (loadTextSuccess) {
                $('#mailTextTab').tab('show');
            }
            $('#mailContentWrapper').removeClass('d-none');
        })
    });
});

function fixEmbedded($parent, msgNumber) {
    fixEmbeddedAttribute($parent, msgNumber, 'src');
}

function fixEmbeddedAttribute($parent, msgNumber, attr) {
    $parent.find('[' + attr + '^="cid:"]').each(function(index, el) {
        var $el = $(el);
        var contentId = $el.attr(attr).substring(4);
        $el.attr(attr, API_ROOT + '/part/' + msgNumber + '/' + contentId);
    });
}

function setIframeContent(iframe, html) {
    iframe.src = 'data:text/html;charset=utf-8,' + encodeURIComponent(encodeEntities(html));
}

function encodeEntities(str) {
    return str.replace(/[\u00A0-\u9999]/g, function(i) {
        return '&#' + i.charCodeAt(0) + ';';
    });
}
