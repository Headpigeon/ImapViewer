var ROOT = BASE_URL + '/mail';
var API_ROOT = ROOT + '/api';

$(document).ready(function() {
    $('#refresh').click(function() {
        document.location.reload();
    });
    
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
            $('#mailHtml').html(html);
            fixEmbedded($('#mailHtml'), msgNumber);
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
