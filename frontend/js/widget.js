console.log('messenger widget loaded!');
function messengerWidget(element_id, params) {
    const baseUrl = params.serverUrl;
    // Формируем URL 
    const iframeUrl = `${baseUrl}`;
    
    const iframeHtml = `
        <iframe 
            src="${iframeUrl}"
            style="width: 100%; height: 500px; border: none;"
            allowfullscreen
            target="_parent" 
            sandbox="allow-same-origin allow-scripts allow-popups allow-forms"
        ></iframe>
    `;
    
    // Вставляем iframe в указанный элемент
    $(`#${element_id}`).html(iframeHtml);
}