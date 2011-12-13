package il.aircon.model;

import il.aircon.controller.ArgumentCantBeNull;

import java.math.BigDecimal;

/**
 * Дополнительные параметры при расчете стоимости
 */
public class CalcProperties {
	
	private Long uid;									//				уникальный идентификатор

	private BigDecimal modulesInstallationCost;			//				стоимость установки внутреннего и наружного блоков
	private Float basePipeLength;						// [метры]		длина магистрали, стоимость которой включена в стоимость установки
	private BigDecimal pipeCost;						// [руб/метр]	стоимость прокладки одного метра магистрали
	private BigDecimal coolantCost;						// [руб/кг]		стоимость хладагента
	private BigDecimal pumpCost;						// [руб]		стоимость дренажной помпы
	private BigDecimal pumpInstallationCost;			// [руб]		стоимость установки дренажной помпы
	
	public BigDecimal getModulesInstallationCost() {
		return modulesInstallationCost;
	}
	
	public void setModulesInstallationCost(BigDecimal modulesInstallationCost) throws IncorrectValueException, ArgumentCantBeNull {
		if (modulesInstallationCost == null) throw new ArgumentCantBeNull("modulesInstallationCost");
		if (modulesInstallationCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("modulesInstallationCost");
		this.modulesInstallationCost = modulesInstallationCost;
	}
	
	public float getBasePipeLength() {
		return basePipeLength;
	}
	
	public void setBasePipeLength(float basePipeLength) throws IncorrectValueException {
		if (basePipeLength < 0) throw new IncorrectValueException("basePipeLength");
		this.basePipeLength = basePipeLength;
	}
	
	public BigDecimal getPipeCost() {
		return pipeCost;
	}
	
	public void setPipeCost(BigDecimal pipeCost) throws ArgumentCantBeNull, IncorrectValueException {
		if (pipeCost == null) throw new ArgumentCantBeNull("pipeCost");
		if (pipeCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("pipeCost");
		this.pipeCost = pipeCost;
	}
	
	public BigDecimal getCoolantCost() {
		return coolantCost;
	}
	
	public void setCoolantCost(BigDecimal coolantCost) throws ArgumentCantBeNull, IncorrectValueException {
		if (coolantCost == null) throw new ArgumentCantBeNull("coolantCost");
		if (coolantCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("coolantCost");
		this.coolantCost = coolantCost;
	}
	
	public BigDecimal getPumpCost() {
		return pumpCost;
	}
	
	public void setPumpCost(BigDecimal pumpCost) throws ArgumentCantBeNull, IncorrectValueException {
		if (pumpCost == null) throw new ArgumentCantBeNull("pumpCost");
		if (pumpCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("pumpCost");
		this.pumpCost = pumpCost;
	}
	
	public BigDecimal getPumpInstallationCost() {
		return pumpInstallationCost;
	}
	
	public void setPumpInstallationCost(BigDecimal pumpInstallationCost) throws ArgumentCantBeNull, IncorrectValueException {
		if (pumpInstallationCost == null) throw new ArgumentCantBeNull("pumpInstallationCost");
		if (pumpInstallationCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("pumpInstallationCost");
		this.pumpInstallationCost = pumpInstallationCost;
	}

	public Long getUid() {
		return uid;
	}
	
	public CalcProperties()
	{
		modulesInstallationCost = new BigDecimal(0);
		basePipeLength = new Float(0);
		pipeCost = new BigDecimal(0);
		coolantCost = new BigDecimal(0);
		pumpCost = new BigDecimal(0);
		pumpInstallationCost = new BigDecimal(0);
	}
}
