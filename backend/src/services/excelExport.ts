import ExcelJS from 'exceljs';
import { Restaurant } from '../models/Restaurant';
import { analyticsService } from './analytics';
import { logger } from '../utils/logger';

interface ChartData {
  labels: string[];
  values: number[];
}

export class ExcelExportService {
  private workbook: ExcelJS.Workbook;

  constructor() {
    this.workbook = new ExcelJS.Workbook();
  }

  private async createSummarySheet(
    worksheet: ExcelJS.Worksheet,
    analytics: any,
    restaurant: Restaurant
  ) {
    // Set column widths
    worksheet.columns = [
      { header: '', width: 30 },
      { header: '', width: 20 },
      { header: '', width: 20 },
      { header: '', width: 20 }
    ];

    // Add title
    worksheet.mergeCells('A1:D2');
    const titleCell = worksheet.getCell('A1');
    titleCell.value = `Restaurant Analytics Report - ${restaurant.name}`;
    titleCell.font = {
      size: 20,
      bold: true,
      color: { argb: '2B5990' }
    };
    titleCell.alignment = { horizontal: 'center', vertical: 'middle' };

    // Add date
    worksheet.mergeCells('A3:D3');
    const dateCell = worksheet.getCell('A3');
    dateCell.value = `Generated on ${new Date().toLocaleDateString()}`;
    dateCell.font = { italic: true };
    dateCell.alignment = { horizontal: 'center' };

    // Add section headers with styling
    const addSectionHeader = (row: number, title: string) => {
      worksheet.mergeCells(`A${row}:D${row}`);
      const cell = worksheet.getCell(`A${row}`);
      cell.value = title;
      cell.font = { size: 14, bold: true, color: { argb: '2B5990' } };
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: 'E6EEF7' }
      };
      worksheet.getRow(row).height = 25;
    };

    // Sales Overview
    addSectionHeader(5, 'Sales Overview');
    this.addMetricRow(worksheet, 6, 'Total Sales', analytics.monthlyMetrics.sales.totalSales, 'currency');
    this.addMetricRow(worksheet, 7, 'Average Order Value', analytics.monthlyMetrics.sales.averageOrderValue, 'currency');
    this.addMetricRow(worksheet, 8, 'Total Orders', analytics.monthlyMetrics.sales.orderCount, 'number');

    // Performance Metrics
    addSectionHeader(10, 'Performance Metrics');
    this.addMetricRow(worksheet, 11, 'Average Preparation Time', analytics.monthlyMetrics.performance.averagePreparationTime, 'time');
    this.addMetricRow(worksheet, 12, 'Average Delivery Time', analytics.monthlyMetrics.performance.averageDeliveryTime, 'time');
    this.addMetricRow(worksheet, 13, 'Order Completion Rate', analytics.monthlyMetrics.performance.orderCompletionRate, 'percentage');
    this.addMetricRow(worksheet, 14, 'Customer Satisfaction', analytics.monthlyMetrics.performance.customerSatisfactionScore, 'rating');

    // Add borders
    worksheet.eachRow({ includeEmpty: true }, (row, rowNumber) => {
      if (rowNumber <= 14) {
        row.eachCell({ includeEmpty: true }, cell => {
          cell.border = {
            top: { style: 'thin' },
            left: { style: 'thin' },
            bottom: { style: 'thin' },
            right: { style: 'thin' }
          };
        });
      }
    });
  }

  private async createSalesChart(
    worksheet: ExcelJS.Worksheet,
    data: ChartData,
    startCell: string,
    title: string
  ) {
    const chart = worksheet.addChart('column' as any, {
      title: {
        text: title,
        font: { size: 12, bold: true, color: { argb: '2B5990' } }
      },
      legend: { position: 'right' },
      plotArea: {
        border: { color: { argb: 'CCCCCC' } }
      },
      dataLabels: { showValue: true },
      style: '18'
    }) as any;

    // Add data series
    chart.addDataSeries({
      name: 'Sales',
      categories: data.labels,
      values: data.values,
      color: '2B5990'
    });

    worksheet.addImage(chart, startCell);
  }

  private async createTopItemsSheet(
    worksheet: ExcelJS.Worksheet,
    analytics: any
  ) {
    worksheet.columns = [
      { header: 'Item Name', key: 'name', width: 30 },
      { header: 'Quantity Sold', key: 'quantity', width: 15 },
      { header: 'Revenue', key: 'revenue', width: 15 }
    ];

    // Style header row
    worksheet.getRow(1).font = { bold: true, color: { argb: 'FFFFFF' } };
    worksheet.getRow(1).fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: '2B5990' }
    };

    // Add data
    analytics.monthlyMetrics.sales.topSellingItems.forEach((item: any, index: number) => {
      worksheet.addRow({
        name: item.name,
        quantity: item.quantity,
        revenue: item.revenue
      });

      // Alternate row colors
      if (index % 2 === 0) {
        worksheet.getRow(index + 2).fill = {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: 'F5F5F5' }
        };
      }
    });

    // Add totals row
    const totalRow = worksheet.addRow({
      name: 'Total',
      quantity: analytics.monthlyMetrics.sales.topSellingItems.reduce((sum: number, item: any) => sum + item.quantity, 0),
      revenue: analytics.monthlyMetrics.sales.topSellingItems.reduce((sum: number, item: any) => sum + item.revenue, 0)
    });
    totalRow.font = { bold: true };
    totalRow.fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: 'E6EEF7' }
    };

    // Add borders
    worksheet.eachRow((row) => {
      row.eachCell((cell) => {
        cell.border = {
          top: { style: 'thin' },
          left: { style: 'thin' },
          bottom: { style: 'thin' },
          right: { style: 'thin' }
        };
      });
    });
  }

  private addMetricRow(
    worksheet: ExcelJS.Worksheet,
    rowNumber: number,
    label: string,
    value: number,
    type: 'currency' | 'number' | 'percentage' | 'time' | 'rating'
  ) {
    const row = worksheet.getRow(rowNumber);
    row.getCell(1).value = label;
    row.getCell(2).value = value;

    switch (type) {
      case 'currency':
        row.getCell(2).numFmt = '$#,##0.00';
        break;
      case 'percentage':
        row.getCell(2).numFmt = '0.0%';
        break;
      case 'time':
        row.getCell(2).value = `${Math.floor(value / 60)}h ${value % 60}m`;
        break;
      case 'rating':
        row.getCell(2).value = `${value.toFixed(1)} / 5.0`;
        break;
    }
  }

  public async generateExcelReport(restaurantId: string): Promise<Buffer> {
    try {
      const analytics = await analyticsService.getRestaurantAnalytics(restaurantId);
      const restaurant = await Restaurant.findById(restaurantId);

      if (!restaurant) {
        throw new Error('Restaurant not found');
      }

      // Create Summary Sheet
      const summarySheet = this.workbook.addWorksheet('Summary');
      await this.createSummarySheet(summarySheet, analytics, restaurant);

      // Create Sales Trends Sheet
      const trendsSheet = this.workbook.addWorksheet('Sales Trends');
      const salesData: ChartData = {
        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
        values: [
          analytics.weeklyMetrics.sales.totalSales / 4,
          analytics.weeklyMetrics.sales.totalSales / 3,
          analytics.weeklyMetrics.sales.totalSales / 2,
          analytics.weeklyMetrics.sales.totalSales
        ]
      };
      await this.createSalesChart(trendsSheet, salesData, 'A1', 'Monthly Sales Trend');

      // Create Top Items Sheet
      const topItemsSheet = this.workbook.addWorksheet('Top Items');
      await this.createTopItemsSheet(topItemsSheet, analytics);

      // Write to buffer
      const buffer = await this.workbook.xlsx.writeBuffer();
      return buffer as Buffer;

    } catch (error) {
      logger.error('Error generating Excel report:', error);
      throw error;
    }
  }
}

export const excelExportService = new ExcelExportService();
