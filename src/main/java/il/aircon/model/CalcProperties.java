package il.aircon.model;

import il.aircon.controller.ArgumentCantBeNull;

import java.math.BigDecimal;

/**
 * Дополнительные параметры при расчете стоимости
 */
public class CalcProperties {
	
	private Long uid;									//				уникальный идентификатор

	private BigDecimal modulesInstallationCost;			//				стоимость установки внутреннего и наружного блоков
	private Float baseTubeLength;						// [метры]		длина магистрали, стоимость которой включена в стоимость установки
	private BigDecimal tubeCost;						// [руб/метр]	стоимость прокладки одного метра магистрали
	private BigDecimal coolantCost;						// [руб/кг]		стоимость хладагента
	private BigDecimal pumpCost;						// [руб]		стоимость дренажной помпы
	
	public BigDecimal getModulesInstallationCost() {
		return modulesInstallationCost;
	}
	public void setModulesInstallationCost(BigDecimal modulesInstallationCost) throws IncorrectValueException, ArgumentCantBeNull {
		if (modulesInstallationCost == null) throw new ArgumentCantBeNull("modulesInstallationCost");
		if (modulesInstallationCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("modulesInstallationCost");
		this.modulesInstallationCost = modulesInstallationCost;
	}
	public float getBaseTubeLength() {
		return baseTubeLength;
	}
	public void setBaseTubeLength(float baseTubeLength) throws IncorrectValueException {
		if (baseTubeLength < 0) throw new IncorrectValueException("baseTubeLength");
		this.baseTubeLength = baseTubeLength;
	}
	public BigDecimal getTubeCost() {
		return tubeCost;
	}
	public void setTubeCost(BigDecimal tubeCost) throws ArgumentCantBeNull, IncorrectValueException {
		if (tubeCost == null) throw new ArgumentCantBeNull("tubeCost");
		if (tubeCost.compareTo(new BigDecimal(0)) < 0) throw new IncorrectValueException("tubeCost");
		this.tubeCost = tubeCost;
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
	public Long getUid() {
		return uid;
	}
	
	public CalcProperties()
	{
		modulesInstallationCost = new BigDecimal(0);
		baseTubeLength = new Float(0);
		tubeCost = new BigDecimal(0);
		coolantCost = new BigDecimal(0);
		pumpCost = new BigDecimal(0);
	}
}
