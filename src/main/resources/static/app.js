const form = document.querySelector('#import-form');
const fileInput = document.querySelector('#file-input');
const fileName = document.querySelector('#file-name');
const resultBox = document.querySelector('#result');
const submitButton = form.querySelector('button[type="submit"]');

fileInput.addEventListener('change', () => {
  const file = fileInput.files[0];
  fileName.textContent = file ? `已选择：${file.name}` : '支持格式：.csv、.xls、.xlsx';
});

form.addEventListener('submit', async (event) => {
  event.preventDefault();

  const file = fileInput.files[0];
  if (!file) {
    renderResult('error', '请选择要导入的 CSV、XLS 或 XLSX 文件。');
    return;
  }

  const formData = new FormData();
  formData.append('file', file);

  submitButton.disabled = true;
  submitButton.textContent = '导入中...';
  renderResult('info', '正在上传并导入账单，请稍候。');

  try {
    const response = await fetch('/bill/import', {
      method: 'POST',
      body: formData,
    });
    const payload = await response.json();

    if (payload.success) {
      const data = payload.data || {};
      renderResult(
        'success',
        `导入成功：总行数 ${data.totalRows ?? 0}，成功 ${data.importedRows ?? 0}，跳过空行 ${data.skippedRows ?? 0}。`
      );
      form.reset();
      fileName.textContent = '支持格式：.csv、.xls、.xlsx';
      return;
    }

    renderResult('error', `导入失败：${payload.message || '未知错误'}（${payload.code || response.status}）`);
  } catch (error) {
    renderResult('error', `请求失败：${error.message}`);
  } finally {
    submitButton.disabled = false;
    submitButton.textContent = '开始导入';
  }
});

function renderResult(type, message) {
  resultBox.className = `result ${type}`;
  resultBox.textContent = message;
}
