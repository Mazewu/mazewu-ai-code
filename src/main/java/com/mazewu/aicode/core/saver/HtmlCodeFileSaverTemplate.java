package com.mazewu.aicode.core.saver;

import cn.hutool.core.util.StrUtil;
import com.mazewu.aicode.ai.model.HtmlCodeResult;
import com.mazewu.aicode.exception.BusinessException;
import com.mazewu.aicode.exception.ErrorCode;
import com.mazewu.aicode.model.enums.CodeGenTypeEnum;

/**
 * HTML代码文件保存器
 *
 * @author Mazewu
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
