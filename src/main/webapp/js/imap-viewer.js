$(document).ready(function() {
    $('#refresh').click(function() {
        document.location.reload();
    });
    
    $('#mailListContent').on('click', '[data-message-number]', null, function(e) {
        var msgNumber = $(e.target).closest('[data-message-number]').attr('data-message-number');

        var loadText = $.get('/mail/api/text/' + msgNumber);
        var loadTextSuccess = false;
        var textLoaded = $.Deferred();
        loadText.done(function(text) {
            $('#mailText').text(text);
            loadTextSuccess = true;
        }).always(function() {
            textLoaded.resolve();
        });

        var loadHtml = $.get('/mail/api/html/' + msgNumber);
        var loadHtmlSuccess = false;
        var htmlLoaded = $.Deferred();
        loadHtml.done(function(html) {
            $('#mailHtml').html(html);
            fixEmbedded($('#mailHtml'), msgNumber);
            loadHtmlSuccess = true;
        }).always(function() {
            htmlLoaded.resolve();
        });

        $.when(textLoaded, htmlLoaded).always(function() {
            $('#mailTextTab').toggleClass('disabled', !loadTextSuccess);
            $('#mailHtmlTab').toggleClass('disabled', !loadHtmlSuccess);

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
        $el.attr(attr, '/mail/api/attachment/' + msgNumber + '/' + contentId);
    });
}
